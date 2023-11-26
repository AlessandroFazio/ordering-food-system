package github.alessandrofazio.order.service.messaging.mapper;

import debezium.payment.order_outbox.Value;
import github.alessandrofazio.domain.event.payload.RestaurantOrderEventPayload;
import github.alessandrofazio.domain.valueobject.OrderApprovalStatus;
import github.alessandrofazio.domain.valueobject.PaymentStatus;
import github.alessandrofazio.kafka.order.avro.model.*;
import github.alessandrofazio.service.domain.dto.message.CustomerModel;
import github.alessandrofazio.service.domain.dto.message.PaymentResponse;
import github.alessandrofazio.service.domain.dto.message.RestaurantApprovalResponse;
import github.alessandrofazio.domain.event.payload.PaymentOrderEventPayload;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OrderMessagingDataMapper {

    public PaymentResponse paymentResponseAvroModelToPaymentResponse(
            PaymentOrderEventPayload paymentOrderEventPayload, Value paymentResponseAvroModel) {
        return PaymentResponse.builder()
                .id(paymentOrderEventPayload.getId())
                .sagaId(paymentOrderEventPayload.getSagaId())
                .paymentId(paymentOrderEventPayload.getPaymentId())
                .customerId(paymentOrderEventPayload.getCustomerId())
                .orderId(paymentOrderEventPayload.getOrderId())
                .price(paymentOrderEventPayload.getPrice())
                .createdAt(Instant.parse(paymentResponseAvroModel.getCreatedAt()))
                .paymentStatus(PaymentStatus.valueOf(paymentOrderEventPayload.getPaymentStatus()))
                .failureMessages(paymentOrderEventPayload.getFailureMessages())
                .build();
    }

    public RestaurantApprovalResponse restaurantOrderEventPayloadToApprovalResponse(
            RestaurantOrderEventPayload restaurantOrderEventPayload,
            debezium.restaurant.order_outbox.Value restaurantApprovalResponseAvroModel) {
        return RestaurantApprovalResponse.builder()
                .id(restaurantApprovalResponseAvroModel.getId())
                .sagaId(restaurantApprovalResponseAvroModel.getSagaId())
                .restaurantId(restaurantOrderEventPayload.getRestaurantId())
                .orderId(restaurantOrderEventPayload.getOrderId())
                .createdAt(restaurantOrderEventPayload.getCreatedAt().toInstant())
                .orderApprovalStatus(OrderApprovalStatus.valueOf(
                        restaurantOrderEventPayload.getOrderApprovalStatus()))
                .failureMessages(restaurantOrderEventPayload.getFailureMessages())
                .build();
    }

    public CustomerModel customerAvroModelToCustomerModel(CustomerAvroModel customerAvroModel) {
        return CustomerModel.builder()
                .id(customerAvroModel.getId().toString())
                .username(customerAvroModel.getUsername())
                .firstName(customerAvroModel.getFirstName())
                .lastName(customerAvroModel.getLastName())
                .build();
    }
}
