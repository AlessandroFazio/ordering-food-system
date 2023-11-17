package github.alessandrofazio.payment.dataaccess.payment.adapter;

import github.alessandrofazio.payment.dataaccess.payment.entity.PaymentEntity;
import github.alessandrofazio.payment.dataaccess.payment.mapper.PaymentDataAccessMapper;
import github.alessandrofazio.payment.dataaccess.payment.repository.PaymentJpaRepository;
import github.alessandrofazio.payment.service.domain.entity.Payment;
import github.alessandrofazio.payment.service.domain.ports.output.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentDataAccessMapper paymentDataAccessMapper;
    @Override
    public Payment save(Payment payment) {
        return paymentDataAccessMapper.paymentEntityToPayment(
                paymentJpaRepository.save(
                        paymentDataAccessMapper.paymentToPaymentEntity(payment)));
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return paymentJpaRepository.findByOrderId(orderId)
                .map(paymentDataAccessMapper::paymentEntityToPayment);
    }
}
