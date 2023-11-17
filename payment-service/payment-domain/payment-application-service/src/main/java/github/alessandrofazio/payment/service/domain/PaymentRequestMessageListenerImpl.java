package github.alessandrofazio.payment.service.domain;

import github.alessandrofazio.payment.service.domain.dto.PaymentRequest;
import github.alessandrofazio.payment.service.domain.event.PaymentCancelledEvent;
import github.alessandrofazio.payment.service.domain.event.PaymentCompletedEvent;
import github.alessandrofazio.payment.service.domain.event.PaymentEvent;
import github.alessandrofazio.payment.service.domain.event.PaymentFailedEvent;
import github.alessandrofazio.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import github.alessandrofazio.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import github.alessandrofazio.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import github.alessandrofazio.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

    private final PaymentRequestHelper paymentRequestHelper;
    private final PaymentCompletedMessagePublisher paymentCompletedMessagePublisher;
    private final PaymentCancelledMessagePublisher paymentCancelledMessagePublisher;
    private final PaymentFailedMessagePublisher paymentFailedMessagePublisher;

    @Override
    public void completePayment(PaymentRequest paymentRequest) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistPayment(paymentRequest);
        fireEvent(paymentEvent);
    }

    @Override
    public void cancelPayment(PaymentRequest paymentRequest) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistCancelPayment(paymentRequest);
        fireEvent(paymentEvent);
    }

    private void fireEvent(PaymentEvent paymentEvent) {
        log.info("Publishing payment event with payment id: {} and order id: {}",
                paymentEvent.getPayment().getId().getValue(),
                paymentEvent.getPayment().getOrderId().getValue());

        if (paymentEvent instanceof PaymentCompletedEvent) {
            paymentCompletedMessagePublisher.publish((PaymentCompletedEvent) paymentEvent);
        } else if (paymentEvent instanceof PaymentCancelledEvent) {
            paymentCancelledMessagePublisher.publish((PaymentCancelledEvent) paymentEvent);
        } else if (paymentEvent instanceof PaymentFailedEvent) {
            paymentFailedMessagePublisher.publish((PaymentFailedEvent) paymentEvent);
        }
    }
}
