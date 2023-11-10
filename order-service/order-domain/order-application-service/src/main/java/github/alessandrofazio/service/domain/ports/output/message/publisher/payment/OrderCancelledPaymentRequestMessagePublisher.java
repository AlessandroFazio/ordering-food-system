package github.alessandrofazio.service.domain.ports.output.message.publisher.payment;

import github.alessandrofazio.domain.event.publisher.DomainEventPublisher;
import github.alessandrofazio.order.service.domain.event.OrderCancelledEvent;

public interface OrderCancelledPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCancelledEvent> {
}
