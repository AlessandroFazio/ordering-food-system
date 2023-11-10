package github.alessandrofazio.order.service.domain;

import github.alessandrofazio.order.service.domain.entity.Order;
import github.alessandrofazio.order.service.domain.entity.Restaurant;
import github.alessandrofazio.order.service.domain.event.OrderCancelledEvent;
import github.alessandrofazio.order.service.domain.event.OrderCreatedEvent;
import github.alessandrofazio.order.service.domain.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {
    OrderCreatedEvent validateAndInitiate(Order order, Restaurant restaurant);

    OrderPaidEvent payOrder(Order order);

    void approveOrder(Order order);

    OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages);
    void cancelOrder(Order order, List<String> failureMessages);
}
