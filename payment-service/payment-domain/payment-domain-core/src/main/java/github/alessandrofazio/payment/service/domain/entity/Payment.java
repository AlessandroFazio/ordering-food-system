package github.alessandrofazio.payment.service.domain.entity;

import github.alessandrofazio.domain.entity.AggregateRoot;
import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.domain.valueobject.Money;
import github.alessandrofazio.domain.valueobject.OrderId;
import github.alessandrofazio.domain.valueobject.PaymentStatus;
import github.alessandrofazio.payment.service.domain.valueobject.PaymentId;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class Payment extends AggregateRoot<PaymentId> {
    private static final String UTC = "UTC";
    private final OrderId orderId;
    private final CustomerId customerId;
    private final Money price;

    private PaymentStatus paymentStatus;
    private ZonedDateTime createdAt;

    public void initializePayment() {
        super.setId(new PaymentId(UUID.randomUUID()));
        createdAt = ZonedDateTime.now(ZoneId.of(UTC));
    }

    public void validatePayment(List<String> failureMessages) {
        if(price == null || !price.isGreaterThanZero()) {
            failureMessages.add("Total price must be greater than zero");
        }
    }

    public void updateStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    private Payment(Builder builder) {
        super.setId(builder.paymentId);
        this.orderId = builder.orderId;
        this.customerId = builder.customerId;
        this.paymentStatus = builder.paymentStatus;
        this.price = builder.price;
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Money getPrice() {
        return price;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public static final class Builder implements github.alessandrofazio.domain.utils.Builder<Payment> {
        private PaymentId paymentId;
        private OrderId orderId;
        private CustomerId customerId;
        private Money price;
        private PaymentStatus paymentStatus;
        private ZonedDateTime createdAt;

        public Builder paymentId(PaymentId paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder price(Money price) {
            this.price = price;
            return this;
        }

        public Builder paymentStatus(PaymentStatus paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        public Builder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @Override
        public Payment build() {
            return new Payment(this);
        }
    }
}
