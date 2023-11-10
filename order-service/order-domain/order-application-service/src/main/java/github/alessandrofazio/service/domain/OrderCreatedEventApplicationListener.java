package github.alessandrofazio.service.domain;

import github.alessandrofazio.order.service.domain.event.OrderCreatedEvent;
import github.alessandrofazio.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEventApplicationListener {
    private final OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher;

    @TransactionalEventListener
    public void process(OrderCreatedEvent orderCreatedEvent) {
        orderCreatedPaymentRequestMessagePublisher.publish(orderCreatedEvent);
    }
}
