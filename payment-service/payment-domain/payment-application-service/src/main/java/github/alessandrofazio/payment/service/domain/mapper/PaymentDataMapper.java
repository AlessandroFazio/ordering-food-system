package github.alessandrofazio.payment.service.domain.mapper;

import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.domain.valueobject.Money;
import github.alessandrofazio.domain.valueobject.OrderId;
import github.alessandrofazio.payment.service.domain.dto.PaymentRequest;
import github.alessandrofazio.payment.service.domain.entity.Payment;
import github.alessandrofazio.payment.service.domain.event.PaymentEvent;
import github.alessandrofazio.payment.service.domain.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentDataMapper {
    public Payment paymentRequestToPayment(PaymentRequest paymentRequest) {
        return Payment.builder()
                .orderId(new OrderId(UUID.fromString(paymentRequest.getOrderId())))
                .customerId(new CustomerId(UUID.fromString(paymentRequest.getCustomerId())))
                .price(new Money(paymentRequest.getPrice()))
                .build();
    }

    public OrderEventPayload paymentEventToOrderEventPayload(PaymentEvent paymentEvent) {
        return OrderEventPayload.builder()
                .paymentId(paymentEvent.getPayment().getId().getValue().toString())
                .orderId(paymentEvent.getPayment().getOrderId().getValue().toString())
                .customerId(paymentEvent.getPayment().getCustomerId().getValue().toString())
                .paymentStatus(paymentEvent.getPayment().getPaymentStatus().name())
                .createdAt(paymentEvent.getCreatedAt())
                .price(paymentEvent.getPayment().getPrice().getAmount())
                .failureMessages(paymentEvent.getFailureMessages())
                .build();
    }
}
