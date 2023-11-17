package github.alessandrofazio.restaurant.service.domain.ports.output.message.publisher;

import github.alessandrofazio.domain.event.publisher.DomainEventPublisher;
import github.alessandrofazio.restaurant.service.domain.event.OrderRejectedEvent;

public interface OrderRejectedMessagePublisher extends DomainEventPublisher<OrderRejectedEvent> {
}
