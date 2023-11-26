package github.alessandrofazio.restaurant.service.domain;

import github.alessandrofazio.restaurant.service.domain.dto.RestaurantApprovalRequest;
import github.alessandrofazio.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantApprovalRequestMessageListenerImpl implements RestaurantApprovalRequestMessageListener {
    private final RestaurantApprovalHelper restaurantApprovalHelper;

    @Override
    public void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest) {
        restaurantApprovalHelper.persistOrderApproval(restaurantApprovalRequest);
    }
}
