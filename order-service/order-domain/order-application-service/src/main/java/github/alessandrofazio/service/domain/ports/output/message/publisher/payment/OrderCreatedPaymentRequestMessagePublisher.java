package github.alessandrofazio.service.domain.ports.output.message.publisher.payment;

import github.alessandrofazio.domain.event.publisher.DomainEventPublisher;
import github.alessandrofazio.order.service.domain.event.OrderCreatedEvent;

public interface OrderCreatedPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCreatedEvent> {
}
