package github.alessandrofazio.restaurant.service.domain.entity;

import github.alessandrofazio.domain.entity.BaseEntity;
import github.alessandrofazio.domain.utils.Builder;
import github.alessandrofazio.domain.valueobject.Money;
import github.alessandrofazio.domain.valueobject.OrderId;
import github.alessandrofazio.domain.valueobject.OrderStatus;

import java.util.List;

public class OrderDetail extends BaseEntity<OrderId> {
    private OrderStatus orderStatus;
    private Money totalAmount;
    private final List<Product> products;

    private OrderDetail(Builder builder) {
        super.setId(builder.orderId);
        orderStatus = builder.orderStatus;
        totalAmount = builder.totalAmount;
        products = builder.products;
    }

    public static Builder builder() {
        return new Builder();
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public List<Product> getProducts() {
        return products;
    }

    public static final class Builder implements github.alessandrofazio.domain.utils.Builder<OrderDetail> {
        private OrderId orderId;
        private OrderStatus orderStatus;
        private Money totalAmount;
        private List<Product> products;
        @Override
        public OrderDetail build() {
            return new OrderDetail(this);
        }

        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder orderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public Builder totalAmount(Money totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder products(List<Product> products) {
            this.products = products;
            return this;
        }
    }
}
