package github.alessandrofazio.order.data.access.order.mapper;

import github.alessandrofazio.domain.valueobject.*;
import github.alessandrofazio.order.data.access.order.entity.OrderAddressEntity;
import github.alessandrofazio.order.data.access.order.entity.OrderEntity;
import github.alessandrofazio.order.data.access.order.entity.OrderItemEntity;
import github.alessandrofazio.order.service.domain.entity.Order;
import github.alessandrofazio.order.service.domain.entity.OrderItem;
import github.alessandrofazio.order.service.domain.entity.Product;
import github.alessandrofazio.order.service.domain.valueobject.OrderItemId;
import github.alessandrofazio.order.service.domain.valueobject.StreetAddress;
import github.alessandrofazio.order.service.domain.valueobject.TrackingId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static github.alessandrofazio.order.data.access.order.entity.OrderEntity.FAILURE_MESSAGE_DELIMITER;

@Component
public class OrderDataAccessMapper {

    public OrderEntity orderToOrderEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .restaurantId(order.getRestaurantId().getValue())
                .trackingId(order.getTrackingId().getValue())
                .address(deliveryAddressToAddressEntity(order.getDeliveryAddress()))
                .price(order.getPrice().getAmount())
                .items(orderItemsToOrderItemEntities(order.getItems()))
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages() != null ?
                        String.join(FAILURE_MESSAGE_DELIMITER, order.getFailureMessages()) : "")
                .build();
        orderEntity.getAddress().setOrder(orderEntity);
        orderEntity.getItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));

        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {
        return Order.builder()
                .orderId(new OrderId(orderEntity.getId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
                .deliveryAddress(addressEntityToDeliveryAddress(orderEntity.getAddress()))
                .price(new Money(orderEntity.getPrice()))
                .items(orderItemEntitiesToOrderItems(orderEntity.getItems()))
                .trackingId(new TrackingId(orderEntity.getTrackingId()))
                .orderStatus(orderEntity.getOrderStatus())
                .failureMessages(orderEntity.getFailureMessages().isEmpty() ? new ArrayList<>() :
                        new ArrayList<>(Arrays.asList(
                                orderEntity.getFailureMessages().split(FAILURE_MESSAGE_DELIMITER))))
                .build();
    }

    private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> items) {
        return items.stream()
                .map(item -> OrderItem.builder()
                        .orderItemId(new OrderItemId(item.getId()))
                        .price(new Money(item.getPrice()))
                        .quantity(item.getQuantity())
                        .product(new Product(new ProductId(item.getProductId())))
                        .subTotal(new Money(item.getSubTotal()))
                        .build())
                .toList();
    }

    private OrderAddressEntity deliveryAddressToAddressEntity(StreetAddress deliveryAddress) {
        return OrderAddressEntity.builder()
                .id(deliveryAddress.getId())
                .street(deliveryAddress.getStreet())
                .postalCode(deliveryAddress.getPostalCode())
                .city(deliveryAddress.getCity())
                .build();
    }

    private List<OrderItemEntity> orderItemsToOrderItemEntities(List<OrderItem> items) {
        return items.stream()
                .map(orderItem -> OrderItemEntity.builder()
                        .id(orderItem.getId().getValue())
                        .price(orderItem.getPrice().getAmount())
                        .productId(orderItem.getProduct().getId().getValue())
                        .subTotal(orderItem.getSubTotal().getAmount())
                        .quantity(orderItem.getQuantity())
                        .build())
                .toList();
    }

    private StreetAddress addressEntityToDeliveryAddress(OrderAddressEntity orderAddress) {
        return new StreetAddress(
                orderAddress.getId(),
                orderAddress.getStreet(),
                orderAddress.getPostalCode(),
                orderAddress.getCity());
    }
}
