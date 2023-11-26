package github.alessandrofazio.service.domain;

import github.alessandrofazio.order.service.domain.event.OrderCancelledEvent;
import github.alessandrofazio.service.domain.dto.message.RestaurantApprovalResponse;
import github.alessandrofazio.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import github.alessandrofazio.service.domain.saga.OrderApprovalSaga;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class RestaurantApprovalResponseMessageListenerImpl implements RestaurantApprovalResponseMessageListener {

    private final OrderApprovalSaga orderApprovalSaga;

    @Override
    public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse) {
        orderApprovalSaga.process(restaurantApprovalResponse);
        log.info("Order is approved for order with id: {}", restaurantApprovalResponse.getOrderId());
    }

    @Override
    public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {
        orderApprovalSaga.rollback(restaurantApprovalResponse);
        log.info("Order Approval Saga rollback operation is completed for order with id: {} with failure messages: {}",
                restaurantApprovalResponse.getOrderId(),
                String.join(",", restaurantApprovalResponse.getFailureMessages()));
    }
}
