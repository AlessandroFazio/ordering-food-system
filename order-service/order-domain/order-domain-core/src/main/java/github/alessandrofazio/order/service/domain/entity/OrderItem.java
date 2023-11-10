package github.alessandrofazio.order.service.domain.entity;

import github.alessandrofazio.domain.entity.BaseEntity;
import github.alessandrofazio.domain.utils.Builder;
import github.alessandrofazio.domain.valueobject.Money;
import github.alessandrofazio.domain.valueobject.OrderId;
import github.alessandrofazio.order.service.domain.valueobject.OrderItemId;

public class OrderItem extends BaseEntity<OrderItemId> {
    private OrderId orderId;
    private final Product product;
    private final int quantity;
    private final Money price;
    private final Money subTotal;

     void initializeOrderItem(OrderId orderId, OrderItemId orderItemId) {
        super.setId(orderItemId);
        this.orderId = orderId;
    }

    boolean isPriceValid() {
         return price.isGreaterThanZero() &&
                 price.equals(product.getPrice()) &&
                 price.multiply(quantity).equals(subTotal);
    }

    private OrderItem(builder builder) {
        super.setId(builder.orderItemId);
        product = builder.product;
        quantity = builder.quantity;
        price = builder.price;
        subTotal = builder.subTotal;
    }

    public static builder builder() {
         return new builder();
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getPrice() {
        return price;
    }

    public Money getSubTotal() {
        return subTotal;
    }

    public static final class builder implements Builder<OrderItem> {
        private OrderItemId orderItemId;
        private Product product;
        private int quantity;
        private Money price;
        private Money subTotal;

        @Override
        public OrderItem build() {
            return new OrderItem(this);
        }

        public builder orderItemId(OrderItemId orderItemId) {
            this.orderItemId = orderItemId;
            return this;
        }

        public builder product(Product product) {
            this.product = product;
            return this;
        }

        public builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public builder price(Money price) {
            this.price = price;
            return this;
        }

        public builder subTotal(Money subTotal) {
            this.subTotal = subTotal;
            return this;
        }
    }
}
