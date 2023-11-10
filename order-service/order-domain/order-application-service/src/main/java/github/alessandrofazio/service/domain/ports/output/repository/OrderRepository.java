package github.alessandrofazio.service.domain.ports.output.repository;

import github.alessandrofazio.order.service.domain.entity.Order;
import github.alessandrofazio.order.service.domain.valueobject.TrackingId;

import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findByTrackingId(TrackingId trackingId);
}
