package github.alessandrofazio.service.domain.saga;

import github.alessandrofazio.domain.valueobject.OrderId;
import github.alessandrofazio.domain.valueobject.OrderStatus;
import github.alessandrofazio.order.service.domain.entity.Order;
import github.alessandrofazio.order.service.domain.exception.OrderNotFoundException;
import github.alessandrofazio.saga.SagaStatus;
import github.alessandrofazio.service.domain.ports.output.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaHelper {

    private final OrderRepository orderRepository;

    Order findOrder(String orderId) {
        Optional<Order> orderResult = orderRepository.findById(new OrderId(UUID.fromString(orderId)));
        if(orderResult.isEmpty()) {
            log.error("Order with id: {} could not be found", orderId);
            throw new OrderNotFoundException("Order with id " + orderId + " could not be found");
        }
        return orderResult.get();
    }

    void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public SagaStatus orderStatusToSagaStatus(OrderStatus orderStatus) {
        return switch (orderStatus) {
            case PENDING -> SagaStatus.STARTED;
            case PAID -> SagaStatus.PROCESSING;
            case APPROVED -> SagaStatus.SUCCEEDED;
            case CANCELLING -> SagaStatus.COMPENSATING;
            case CANCELLED -> SagaStatus.COMPENSATED;
        };
    }
}
