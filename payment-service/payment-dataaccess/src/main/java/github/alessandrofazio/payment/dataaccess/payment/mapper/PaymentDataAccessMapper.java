package github.alessandrofazio.payment.dataaccess.payment.mapper;

import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.domain.valueobject.Money;
import github.alessandrofazio.domain.valueobject.OrderId;
import github.alessandrofazio.payment.dataaccess.payment.entity.PaymentEntity;
import github.alessandrofazio.payment.service.domain.entity.Payment;
import github.alessandrofazio.payment.service.domain.valueobject.PaymentId;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataAccessMapper {

    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId().getValue())
                .customerId(payment.getCustomerId().getValue())
                .orderId(payment.getOrderId().getValue())
                .price(payment.getPrice().getAmount())
                .status(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
        return Payment.builder()
                .paymentId(new PaymentId(paymentEntity.getId()))
                .customerId(new CustomerId(paymentEntity.getCustomerId()))
                .orderId(new OrderId(paymentEntity.getOrderId()))
                .price(new Money(paymentEntity.getPrice()))
                .createdAt(paymentEntity.getCreatedAt())
                .build();
    }
}
