package github.alessandrofazio.restaurant.service.messaging.mapper;

import github.alessandrofazio.domain.valueobject.ProductId;
import github.alessandrofazio.domain.valueobject.RestaurantOrderStatus;
import github.alessandrofazio.kafka.order.avro.model.OrderApprovalStatus;
import github.alessandrofazio.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import github.alessandrofazio.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import github.alessandrofazio.restaurant.service.domain.dto.RestaurantApprovalRequest;
import github.alessandrofazio.restaurant.service.domain.entity.Product;
import github.alessandrofazio.restaurant.service.domain.event.OrderApprovedEvent;
import github.alessandrofazio.restaurant.service.domain.event.OrderRejectedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RestaurantMessagingDataMapper {
    public RestaurantApprovalResponseAvroModel orderApprovedEventTorestaurantApprovalResponseAvroModel(
            OrderApprovedEvent orderApprovedEvent) {
        return RestaurantApprovalResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSageId(UUID.randomUUID())
                .setOrderId(orderApprovedEvent.getOrderApproval().getOrderId().getValue())
                .setRestaurantId(orderApprovedEvent.getRestaurantId().getValue())
                .setCreatedAt(orderApprovedEvent.getCreatedAt().toInstant())
                .setOrderApprovalStatus(OrderApprovalStatus.valueOf(
                        orderApprovedEvent.getOrderApproval().getOrderApprovalStatus().name()))
                .setFailureMessages(orderApprovedEvent.getFailureMessages())
                .build();
    }

    public RestaurantApprovalResponseAvroModel orderRejectedEventToRestaurantApprovalResponseAvroModel(
            OrderRejectedEvent orderRejectedEvent) {
        return RestaurantApprovalResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSageId(UUID.randomUUID())
                .setOrderId(orderRejectedEvent.getOrderApproval().getOrderId().getValue())
                .setRestaurantId(orderRejectedEvent.getRestaurantId().getValue())
                .setCreatedAt(orderRejectedEvent.getCreatedAt().toInstant())
                .setOrderApprovalStatus(OrderApprovalStatus.valueOf(
                        orderRejectedEvent.getOrderApproval().getOrderApprovalStatus().name()))
                .setFailureMessages(orderRejectedEvent.getFailureMessages())
                .build();
    }

    public RestaurantApprovalRequest restaurantApprovalRequestAvroModelToRestaurantApprovalRequest(
            RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel) {
        return RestaurantApprovalRequest.builder()
                .id(restaurantApprovalRequestAvroModel.getId().toString())
                .sagaId(restaurantApprovalRequestAvroModel.getSageId().toString())
                .restaurantId(restaurantApprovalRequestAvroModel.getRestaurantId().toString())
                .orderId(restaurantApprovalRequestAvroModel.getOrderId().toString())
                .createdAt(restaurantApprovalRequestAvroModel.getCreatedAt())
                .price(restaurantApprovalRequestAvroModel.getPrice())
                .restaurantOrderStatus(RestaurantOrderStatus.valueOf(
                        restaurantApprovalRequestAvroModel.getRestaurantOrderStatus().name()))
                .products(restaurantApprovalRequestAvroModel.getProducts().stream()
                        .map(productAvroModel ->
                                Product.builder()
                                        .productId(new ProductId(UUID.fromString(productAvroModel.getId())))
                                        .quantity(productAvroModel.getQuantity())
                                        .build()).toList())
                .build();
    }
}
