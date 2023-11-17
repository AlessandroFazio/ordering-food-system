package github.alessandrofazio.restaurant.service.domain;

import github.alessandrofazio.restaurant.service.domain.dto.RestaurantApprovalRequest;
import github.alessandrofazio.restaurant.service.domain.event.OrderApprovalEvent;
import github.alessandrofazio.restaurant.service.domain.event.OrderApprovedEvent;
import github.alessandrofazio.restaurant.service.domain.event.OrderRejectedEvent;
import github.alessandrofazio.restaurant.service.domain.helper.RestaurantApprovalHelper;
import github.alessandrofazio.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import github.alessandrofazio.restaurant.service.domain.ports.output.message.publisher.OrderApprovedMessagePublisher;
import github.alessandrofazio.restaurant.service.domain.ports.output.message.publisher.OrderRejectedMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantApprovalRequestMessageListenerImpl implements RestaurantApprovalRequestMessageListener {
    private final RestaurantApprovalHelper restaurantApprovalHelper;
    private final OrderApprovedMessagePublisher orderApprovedMessagePublisher;
    private final OrderRejectedMessagePublisher orderRejectedMessagePublisher;

    @Override
    public void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest) {
        OrderApprovalEvent orderApprovalEvent =
                restaurantApprovalHelper.persistOrderApproval(restaurantApprovalRequest);
        fireEvent(orderApprovalEvent);
    }

    private void fireEvent(OrderApprovalEvent orderApprovalEvent) {
        log.info("Received restaurantApprovalEvent for order with id: {} for restaurant with id: {}",
                orderApprovalEvent.getOrderApproval().getOrderId().getValue(),
                orderApprovalEvent.getRestaurantId().getValue());

        if(orderApprovalEvent instanceof OrderApprovedEvent) {
            orderApprovedMessagePublisher.publish((OrderApprovedEvent) orderApprovalEvent);
        } else if(orderApprovalEvent instanceof OrderRejectedEvent) {
            orderRejectedMessagePublisher.publish((OrderRejectedEvent) orderApprovalEvent);
        }
    }
}
