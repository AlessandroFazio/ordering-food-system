package github.alessandrofazio.service.domain;

import github.alessandrofazio.domain.event.EmptyEvent;
import github.alessandrofazio.order.service.domain.OrderDomainService;
import github.alessandrofazio.order.service.domain.event.OrderCancelledEvent;
import github.alessandrofazio.saga.SagaStep;
import github.alessandrofazio.service.domain.dto.message.RestaurantApprovalResponse;
import github.alessandrofazio.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import github.alessandrofazio.service.domain.ports.output.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse, EmptyEvent, OrderCancelledEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher;

    @Override
    @Transactional
    public EmptyEvent process(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Approving order with id: {}", restaurantApprovalResponse.getOrderId());

        return null;
    }

    @Override
    @Transactional
    public OrderCancelledEvent rollback(RestaurantApprovalResponse data) {
        return null;
    }
}
