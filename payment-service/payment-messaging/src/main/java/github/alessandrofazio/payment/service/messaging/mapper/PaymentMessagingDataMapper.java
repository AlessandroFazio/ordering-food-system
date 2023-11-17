package github.alessandrofazio.payment.service.messaging.mapper;

import github.alessandrofazio.domain.valueobject.PaymentOrderStatus;
import github.alessandrofazio.kafka.order.avro.model.PaymentRequestAvroModel;
import github.alessandrofazio.kafka.order.avro.model.PaymentResponseAvroModel;
import github.alessandrofazio.kafka.order.avro.model.PaymentStatus;
import github.alessandrofazio.payment.service.domain.dto.PaymentRequest;
import github.alessandrofazio.payment.service.domain.event.PaymentCancelledEvent;
import github.alessandrofazio.payment.service.domain.event.PaymentCompletedEvent;
import github.alessandrofazio.payment.service.domain.event.PaymentFailedEvent;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {
    public PaymentResponseAvroModel paymentCompletedEventTopaymentResponseAvroModel(
            PaymentCompletedEvent paymentCompletedEvent) {
        return PaymentResponseAvroModel.newBuilder()
                .setPaymentId(paymentCompletedEvent.getPayment().getId().getValue())
                .setSageId(UUID.randomUUID())
                .setOrderId(paymentCompletedEvent.getPayment().getOrderId().getValue())
                .setCustomerId(paymentCompletedEvent.getPayment().getCustomerId().getValue())
                .setPrice(paymentCompletedEvent.getPayment().getPrice().getAmount())
                .setCreatedAt(paymentCompletedEvent.getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(paymentCompletedEvent.getPayment().getPaymentStatus().name()))
                .setFailureMessages(paymentCompletedEvent.getFailureMessages())
                .build();
    }

    public PaymentResponseAvroModel paymentCancelledEventTopaymentResponseAvroModel(
            PaymentCancelledEvent paymentCancelledEvent) {
        return PaymentResponseAvroModel.newBuilder()
                .setPaymentId(paymentCancelledEvent.getPayment().getId().getValue())
                .setSageId(UUID.randomUUID())
                .setOrderId(paymentCancelledEvent.getPayment().getOrderId().getValue())
                .setCustomerId(paymentCancelledEvent.getPayment().getCustomerId().getValue())
                .setPrice(paymentCancelledEvent.getPayment().getPrice().getAmount())
                .setCreatedAt(paymentCancelledEvent.getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(paymentCancelledEvent.getPayment().getPaymentStatus().name()))
                .setFailureMessages(paymentCancelledEvent.getFailureMessages())
                .build();
    }

    public PaymentResponseAvroModel paymentFailedEventTopaymentResponseAvroModel(
            PaymentFailedEvent paymentFailedEvent) {
        return PaymentResponseAvroModel.newBuilder()
                .setPaymentId(paymentFailedEvent.getPayment().getId().getValue())
                .setSageId(UUID.randomUUID())
                .setOrderId(paymentFailedEvent.getPayment().getOrderId().getValue())
                .setCustomerId(paymentFailedEvent.getPayment().getCustomerId().getValue())
                .setPrice(paymentFailedEvent.getPayment().getPrice().getAmount())
                .setCreatedAt(paymentFailedEvent.getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(paymentFailedEvent.getPayment().getPaymentStatus().name()))
                .setFailureMessages(paymentFailedEvent.getFailureMessages())
                .build();
    }

    public PaymentRequest paymentRequestAvroModelTopaymentRequest(
            PaymentRequestAvroModel paymentRequestAvroModel) {
        return PaymentRequest.builder()
                .id(paymentRequestAvroModel.getId().toString())
                .sagaId(paymentRequestAvroModel.getSageId().toString())
                .customerId(paymentRequestAvroModel.getCustomerId().toString())
                .orderId(paymentRequestAvroModel.getOrderId().toString())
                .price(paymentRequestAvroModel.getPrice())
                .createdAt(paymentRequestAvroModel.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.valueOf(paymentRequestAvroModel.getPaymentOrderStatus().name()))
                .build();
    }
}
