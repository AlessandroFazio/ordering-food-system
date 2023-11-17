package github.alessandrofazio.restaurant.service.domain.ports.input.message.listener;

import github.alessandrofazio.restaurant.service.domain.dto.RestaurantApprovalRequest;

public interface RestaurantApprovalRequestMessageListener {
    void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest);
}
