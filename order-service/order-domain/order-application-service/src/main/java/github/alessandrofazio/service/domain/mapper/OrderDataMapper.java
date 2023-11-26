package github.alessandrofazio.service.domain.mapper;

import github.alessandrofazio.domain.valueobject.*;
import github.alessandrofazio.order.service.domain.entity.*;
import github.alessandrofazio.order.service.domain.event.OrderCancelledEvent;
import github.alessandrofazio.order.service.domain.event.OrderCreatedEvent;
import github.alessandrofazio.order.service.domain.event.OrderPaidEvent;
import github.alessandrofazio.order.service.domain.valueobject.StreetAddress;
import github.alessandrofazio.service.domain.dto.create.CreateOrderCommand;
import github.alessandrofazio.service.domain.dto.create.CreateOrderResponse;
import github.alessandrofazio.service.domain.dto.create.OrderAddress;
import github.alessandrofazio.service.domain.dto.message.CustomerModel;
import github.alessandrofazio.service.domain.dto.track.TrackOrderResponse;
import github.alessandrofazio.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import github.alessandrofazio.service.domain.outbox.model.approval.OrderApprovalEventProduct;
import github.alessandrofazio.domain.event.payload.OrderPaymentEventPayload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderDataMapper {
    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(createOrderCommand.getItems().stream().map(orderItem ->
                        new Product(new ProductId(orderItem.getProductId())))
                        .toList())
                .build();
    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.getOrderAddress()))
                .price(new Money(createOrderCommand.getPrice()))
                .items(orderItemsToOrderItemEntity(createOrderCommand.getItems()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build();
    }

    private List<OrderItem> orderItemsToOrderItemEntity(
            List<github.alessandrofazio.service.domain.dto.create.OrderItem> orderItems) {
        return orderItems.stream().map(orderItem ->
                OrderItem.builder()
                        .product(new Product(new ProductId(orderItem.getProductId())))
                        .price(new Money(orderItem.getPrice()))
                        .quantity(orderItem.getQuantity())
                        .subTotal(new Money(orderItem.getSubTotal()))
                        .build())
                .toList();
    }

    private StreetAddress orderAddressToStreetAddress(OrderAddress orderAddress) {
        return new StreetAddress(
                UUID.randomUUID(),
                orderAddress.getStreet(),
                orderAddress.getPostalCode(),
                orderAddress.getCity());
    }

    public TrackOrderResponse orderToTrackOrderResponse(Order order) {
        return TrackOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build();
    }

    public OrderPaymentEventPayload orderCreatedEventToOrderPaymentEventPayload(
            OrderCreatedEvent orderCreatedEvent) {
        return OrderPaymentEventPayload.builder()
                .customerId(orderCreatedEvent.getOrder().getCustomerId().getValue().toString())
                .orderId(orderCreatedEvent.getOrder().getId().getValue().toString())
                .createdAt(orderCreatedEvent.getCreatedAt())
                .price(orderCreatedEvent.getOrder().getPrice().getAmount())
                .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
                .build();
    }

    public OrderApprovalEventPayload orderPaidEventToOrderApprovalEventPayload(OrderPaidEvent orderPaidEvent) {
        return OrderApprovalEventPayload.builder()
                .orderId(orderPaidEvent.getOrder().getId().getValue().toString())
                .restaurantId(orderPaidEvent.getOrder().getRestaurantId().getValue().toString())
                .restaurantOrderStatus(RestaurantOrderStatus.PAID.name())
                .products(orderPaidEvent.getOrder().getItems().stream().map(orderItem ->
                        OrderApprovalEventProduct.builder()
                                .id(orderItem.getProduct().getId().getValue().toString())
                                .quantity(orderItem.getQuantity())
                                .build()).toList())
                .createdAt(orderPaidEvent.getCreatedAt())
                .price(orderPaidEvent.getOrder().getPrice().getAmount())
                .build();
    }

    public OrderPaymentEventPayload orderCancelledToOrderPaymentEventPayload(OrderCancelledEvent orderCancelledEvent) {
        return OrderPaymentEventPayload.builder()
                .orderId(orderCancelledEvent.getOrder().getId().getValue().toString())
                .customerId(orderCancelledEvent.getOrder().getCustomerId().getValue().toString())
                .price(orderCancelledEvent.getOrder().getPrice().getAmount())
                .createdAt(orderCancelledEvent.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.CANCELLED.name())
                .build();
    }

    public Customer CustomerModelToCustomer(CustomerModel customerModel) {
        return new Customer(
                new CustomerId(UUID.fromString(customerModel.getId())),
                new Username(customerModel.getUsername()),
                CustomerInformation.builder()
                        .firstName(customerModel.getFirstName())
                        .lastName(customerModel.getLastName())
                        .build());
    }
}
