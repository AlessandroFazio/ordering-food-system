package github.alessandrofazio.payment.service.messaging.mapper;

import debezium.order.payment_outbox.Value;
import github.alessandrofazio.domain.event.payload.OrderPaymentEventPayload;
import github.alessandrofazio.domain.valueobject.PaymentOrderStatus;
import github.alessandrofazio.payment.service.domain.dto.PaymentRequest;
import org.springframework.stereotype.Component;

@Component
public class PaymentMessagingDataMapper {

    public PaymentRequest orderPaymentEventPayloadTopaymentRequest(
            OrderPaymentEventPayload orderPaymentEventPayload,
            Value paymentRequestAvroModel) {
        return PaymentRequest.builder()
                .id(paymentRequestAvroModel.getId())
                .sagaId(paymentRequestAvroModel.getSagaId())
                .customerId(orderPaymentEventPayload.getCustomerId())
                .orderId(orderPaymentEventPayload.getOrderId())
                .price(orderPaymentEventPayload.getPrice())
                .createdAt(orderPaymentEventPayload.getCreatedAt().toInstant())
                .paymentOrderStatus(PaymentOrderStatus.valueOf(orderPaymentEventPayload.getPaymentOrderStatus()))
                .build();
    }
}
