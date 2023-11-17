package github.alessandrofazio.restaurant.service.domain;

import github.alessandrofazio.domain.valueobject.OrderApprovalStatus;
import github.alessandrofazio.restaurant.service.domain.entity.Restaurant;
import github.alessandrofazio.restaurant.service.domain.event.OrderApprovalEvent;
import github.alessandrofazio.restaurant.service.domain.event.OrderApprovedEvent;
import github.alessandrofazio.restaurant.service.domain.event.OrderRejectedEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static github.alessandrofazio.domain.constant.DomainConstants.UTC;

@Slf4j
public class RestaurantDomainServiceImpl implements RestaurantDomainService {
    @Override
    public OrderApprovalEvent validateOrder(Restaurant restaurant,
                                            List<String> failureMessages) {
        restaurant.validateOrder(failureMessages);
        log.info("Validating order with id: {}", restaurant.getOrderDetail().getId().getValue());

        if(failureMessages.isEmpty()) {
            log.info("Order is approved for order id: {}", restaurant.getOrderDetail().getId().getValue());
            restaurant.constructOrderApproval(OrderApprovalStatus.APPROVED);
            return new OrderApprovedEvent(restaurant.getOrderApproval(),
                    restaurant.getId(), failureMessages, LocalDateTime.now().atZone(ZoneId.of(UTC)));
        }
        else {
            log.info("Order is rejected for order with id: {}", restaurant.getOrderDetail().getId().getValue());
            restaurant.constructOrderApproval(OrderApprovalStatus.REJECTED);
            return new OrderRejectedEvent(restaurant.getOrderApproval(),
                    restaurant.getId(), failureMessages, LocalDateTime.now().atZone(ZoneId.of(UTC)));
        }
    }
}
