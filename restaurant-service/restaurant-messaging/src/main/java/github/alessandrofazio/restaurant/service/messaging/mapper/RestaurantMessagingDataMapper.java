package github.alessandrofazio.restaurant.service.messaging.mapper;

import debezium.order.restaurant_approval_outbox.Value;
import github.alessandrofazio.domain.event.payload.OrderApprovalEventPayload;
import github.alessandrofazio.domain.valueobject.ProductId;
import github.alessandrofazio.domain.valueobject.RestaurantOrderStatus;
import github.alessandrofazio.restaurant.service.domain.dto.RestaurantApprovalRequest;
import github.alessandrofazio.restaurant.service.domain.entity.Product;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RestaurantMessagingDataMapper {

    public RestaurantApprovalRequest restaurantOrderEventPayloadToRestaurantApprovalRequest(
            OrderApprovalEventPayload restaurantOrderEventPayload,
            Value restaurantApprovalRequestAvroModel) {
        return RestaurantApprovalRequest.builder()
                .id(restaurantApprovalRequestAvroModel.getId())
                .sagaId(restaurantApprovalRequestAvroModel.getSagaId())
                .restaurantId(restaurantOrderEventPayload.getRestaurantId())
                .orderId(restaurantOrderEventPayload.getOrderId())
                .createdAt(restaurantOrderEventPayload.getCreatedAt().toInstant())
                .price(restaurantOrderEventPayload.getPrice())
                .restaurantOrderStatus(RestaurantOrderStatus.valueOf(
                        restaurantOrderEventPayload.getRestaurantOrderStatus()))
                .products(restaurantOrderEventPayload.getProducts().stream()
                        .map(productAvroModel ->
                                Product.builder()
                                        .productId(new ProductId(UUID.fromString(productAvroModel.getId())))
                                        .quantity(productAvroModel.getQuantity())
                                        .build()).toList())
                .build();
    }
}
