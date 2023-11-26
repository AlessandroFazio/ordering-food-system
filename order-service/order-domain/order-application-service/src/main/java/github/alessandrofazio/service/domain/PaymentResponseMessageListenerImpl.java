package github.alessandrofazio.service.domain;

import github.alessandrofazio.order.service.domain.event.OrderPaidEvent;
import github.alessandrofazio.service.domain.dto.message.PaymentResponse;
import github.alessandrofazio.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import github.alessandrofazio.service.domain.saga.OrderPaymentSaga;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {

    private final OrderPaymentSaga orderPaymentSaga;

    @Override
    public void paymentCompleted(PaymentResponse paymentResponse) {
        orderPaymentSaga.process(paymentResponse);
        log.info("Order Payment Saga process operation is completed for order with id: {}",
                paymentResponse.getOrderId());
    }

    @Override
    public void paymentCancelled(PaymentResponse paymentResponse) {
        orderPaymentSaga.rollback(paymentResponse);
        log.info("Order with order id: {} is rolled back with failure messages: {}",
                paymentResponse.getOrderId(),
                String.join(",", paymentResponse.getFailureMessages()));
    }
}
