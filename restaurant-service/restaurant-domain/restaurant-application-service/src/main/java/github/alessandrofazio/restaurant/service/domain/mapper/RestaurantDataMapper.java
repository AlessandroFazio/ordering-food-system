package github.alessandrofazio.restaurant.service.domain.mapper;

import github.alessandrofazio.domain.valueobject.*;
import github.alessandrofazio.restaurant.service.domain.dto.RestaurantApprovalRequest;
import github.alessandrofazio.restaurant.service.domain.entity.OrderDetail;
import github.alessandrofazio.restaurant.service.domain.entity.Product;
import github.alessandrofazio.restaurant.service.domain.entity.Restaurant;
import github.alessandrofazio.restaurant.service.domain.event.OrderApprovalEvent;
import github.alessandrofazio.restaurant.service.domain.outbox.model.OrderEventPayload;
import github.alessandrofazio.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RestaurantDataMapper {
    public Restaurant restaurantApprovalRequestToRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(UUID.fromString(restaurantApprovalRequest.getRestaurantId())))
                .orderDetail(OrderDetail.builder()
                        .orderId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())))
                        .products(restaurantApprovalRequest.getProducts().stream().map(
                                (Product product) -> Product.builder()
                                        .productId(product.getId())
                                        .quantity(product.getQuantity())
                                        .build()).toList())
                        .totalAmount(new Money(restaurantApprovalRequest.getPrice()))
                        .orderStatus(OrderStatus.valueOf(restaurantApprovalRequest.getRestaurantOrderStatus().name()))
                        .build())
                .build();
    }

    public OrderEventPayload orderApprovalEventToOrderEventPayload(OrderApprovalEvent orderApprovalEvent) {
        return OrderEventPayload.builder()
                .restaurantId(orderApprovalEvent.getRestaurantId().getValue().toString())
                .orderId(orderApprovalEvent.getOrderApproval().getOrderId().getValue().toString())
                .orderApprovalStatus(orderApprovalEvent.getOrderApproval().getOrderApprovalStatus().name())
                .createdAt(orderApprovalEvent.getCreatedAt())
                .failureMessages(orderApprovalEvent.getFailureMessages())
                .build();
    }
}
