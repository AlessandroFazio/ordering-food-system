package github.alessandrofazio.service.domain;

import github.alessandrofazio.order.service.domain.entity.Order;
import github.alessandrofazio.order.service.domain.exception.OrderNotFoundException;
import github.alessandrofazio.order.service.domain.valueobject.TrackingId;
import github.alessandrofazio.service.domain.dto.track.TrackOrderResponse;
import github.alessandrofazio.service.domain.dto.track.TrackerOrderQuery;
import github.alessandrofazio.service.domain.mapper.OrderDataMapper;
import github.alessandrofazio.service.domain.ports.output.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTrackCommandHandler {

    private final OrderDataMapper orderDataMapper;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public TrackOrderResponse trackOrder(TrackerOrderQuery trackerOrderQuery) {
        Optional<Order> orderResult = orderRepository.findByTrackingId(new TrackingId(trackerOrderQuery.getOrderTrackingId()));
        if(orderResult.isEmpty()) {
            log.warn("Could not find order with tracking id: {}", trackerOrderQuery.getOrderTrackingId());
            throw new OrderNotFoundException("Could not find order with tracking id: " +
                    trackerOrderQuery.getOrderTrackingId());
        }

        return orderDataMapper.orderToTrackOrderResponse(orderResult.get());
    }
}
