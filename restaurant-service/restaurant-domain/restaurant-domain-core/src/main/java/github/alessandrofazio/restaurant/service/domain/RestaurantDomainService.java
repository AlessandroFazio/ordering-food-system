package github.alessandrofazio.restaurant.service.domain;

import github.alessandrofazio.restaurant.service.domain.entity.Restaurant;
import github.alessandrofazio.restaurant.service.domain.event.OrderApprovalEvent;

import java.util.List;

public interface RestaurantDomainService {
    OrderApprovalEvent validateOrder(Restaurant restaurant, List<String> failureMessages);
}
