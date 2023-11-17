package github.alessandrofazio.order.data.access.order.adapter;

import github.alessandrofazio.order.data.access.order.entity.OrderEntity;
import github.alessandrofazio.order.data.access.order.mapper.OrderDataAccessMapper;
import github.alessandrofazio.order.data.access.order.repository.OrderJpaRepository;
import github.alessandrofazio.order.service.domain.entity.Order;
import github.alessandrofazio.order.service.domain.valueobject.TrackingId;
import github.alessandrofazio.service.domain.ports.output.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = orderJpaRepository.save(
                orderDataAccessMapper.orderToOrderEntity(order));
        return orderDataAccessMapper.orderEntityToOrder(orderEntity);
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        return orderJpaRepository.findByTrackingId(trackingId.getValue())
                .map(orderDataAccessMapper::orderEntityToOrder);
    }
}
