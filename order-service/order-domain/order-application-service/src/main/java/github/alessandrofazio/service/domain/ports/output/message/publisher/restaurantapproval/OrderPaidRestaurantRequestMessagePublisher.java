package github.alessandrofazio.service.domain.ports.output.message.publisher.restaurantapproval;

import github.alessandrofazio.domain.event.publisher.DomainEventPublisher;
import github.alessandrofazio.order.service.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
