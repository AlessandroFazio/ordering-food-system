package github.alessandrofazio.restaurant.service.domain.event;

import github.alessandrofazio.domain.valueobject.RestaurantId;
import github.alessandrofazio.restaurant.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderApprovedEvent extends OrderApprovalEvent {
    public OrderApprovedEvent(OrderApproval orderApproval,
                              RestaurantId restaurantId,
                              List<String> failureMessages,
                              ZonedDateTime createdAt) {
        super(orderApproval, restaurantId, failureMessages, createdAt);
    }
}
