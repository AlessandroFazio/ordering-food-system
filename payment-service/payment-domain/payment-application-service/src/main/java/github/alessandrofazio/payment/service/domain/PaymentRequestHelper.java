package github.alessandrofazio.payment.service.domain;

import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.payment.service.domain.dto.PaymentRequest;
import github.alessandrofazio.payment.service.domain.entity.CreditEntry;
import github.alessandrofazio.payment.service.domain.entity.CreditHistory;
import github.alessandrofazio.payment.service.domain.entity.Payment;
import github.alessandrofazio.payment.service.domain.event.PaymentEvent;
import github.alessandrofazio.payment.service.domain.exception.PaymentApplicationServiceException;
import github.alessandrofazio.payment.service.domain.mapper.PaymentDataMapper;
import github.alessandrofazio.payment.service.domain.ports.output.repository.CreditEntryRepository;
import github.alessandrofazio.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import github.alessandrofazio.payment.service.domain.ports.output.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestHelper {

    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;

    @Transactional
    public PaymentEvent persistPayment(PaymentRequest paymentRequest) {
        log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());
        Payment payment = paymentDataMapper.paymentRequestToPayment(paymentRequest);
        Function<PaymentInfo, PaymentEvent> validateAndInitiateFunc = info -> paymentDomainService
                .validateAndInitiatePayment(info.payment, info.creditEntry, info.creditHistories, info.failureMessages);
        return validateAndPersistPayment(payment, validateAndInitiateFunc);
    }

    public PaymentEvent persistCancelPayment(PaymentRequest paymentRequest) {
        log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());
        Optional<Payment> paymentResponse = paymentRepository
                .findByOrderId(UUID.fromString(paymentRequest.getOrderId()));
        if(paymentResponse.isEmpty()) {
            log.error("Payment with order id: {} could not be found", paymentRequest.getOrderId());
            throw new PaymentApplicationServiceException("Payment with order id: " +
                    paymentRequest.getOrderId() + " could not be found");
        }
        Payment payment = paymentResponse.get();
        Function<PaymentInfo, PaymentEvent> validateAndCancelFunc = info -> paymentDomainService
                .validateAndCancelPayment(info.payment, info.creditEntry, info.creditHistories, info.failureMessages);
        return validateAndPersistPayment(payment, validateAndCancelFunc);
    }

    private PaymentEvent validateAndPersistPayment(Payment payment, Function<PaymentInfo, PaymentEvent> paymentEventFunction) {
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentEventFunction.apply(
                new PaymentInfo(payment, creditEntry, creditHistories, failureMessages));
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages);
        return paymentEvent;
    }

    private void persistDbObjects(Payment payment, CreditEntry creditEntry, List<CreditHistory> creditHistories, List<String> failureMessages) {
        paymentRepository.save(payment);
        if(failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
        }
    }

    private List<CreditHistory> getCreditHistory(CustomerId customerId) {
        List<CreditHistory> creditHistories = creditHistoryRepository.findByCustomerId(customerId);
        if(creditHistories.isEmpty()) {
            throw new ApplicationContextException("Could not find credit histories for customer: " +
                    customerId.getValue());
        }
        return creditHistories;
    }

    private CreditEntry getCreditEntry(CustomerId customerId) {
        Optional<CreditEntry> creditEntry = creditEntryRepository.findByCustomerId(customerId);
        if(creditEntry.isEmpty()) {
            throw new PaymentApplicationServiceException("Could not find credit entry for customer: " +
                    customerId.getValue());
        }
        return creditEntry.get();
    }

    private record PaymentInfo(Payment payment,
                               CreditEntry creditEntry,
                               List<CreditHistory> creditHistories,
                               List<String> failureMessages) {
    }
}
