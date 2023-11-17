package github.alessandrofazio.restaurant.service.domain.ports.output.message.publisher;

import github.alessandrofazio.domain.event.publisher.DomainEventPublisher;
import github.alessandrofazio.restaurant.service.domain.event.OrderApprovedEvent;

public interface OrderApprovedMessagePublisher extends DomainEventPublisher<OrderApprovedEvent> {
}
