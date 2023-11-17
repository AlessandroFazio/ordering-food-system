package github.alessandrofazio.restaurant.service.domain.entity;

import github.alessandrofazio.domain.entity.AggregateRoot;
import github.alessandrofazio.domain.utils.Builder;
import github.alessandrofazio.domain.valueobject.Money;
import github.alessandrofazio.domain.valueobject.OrderApprovalStatus;
import github.alessandrofazio.domain.valueobject.OrderStatus;
import github.alessandrofazio.domain.valueobject.RestaurantId;
import github.alessandrofazio.restaurant.service.domain.valueobject.OrderApprovalId;

import java.util.List;
import java.util.UUID;

public class Restaurant extends AggregateRoot<RestaurantId> {
    private OrderApproval orderApproval;
    private boolean active;
    private final OrderDetail orderDetail;

    public void validateOrder(List<String> failureMessages) {
        if(orderDetail.getOrderStatus() != OrderStatus.PAID) {
            failureMessages.add("Payment is not completed for order: " + orderDetail.getId());
        }
        Money totalAmount = orderDetail.getProducts().stream()
                .map(product -> {
                    if(!product.isAvailable()) {
                        failureMessages.add("Product with id: " + product.getId().getValue()
                                + " is not available");
                    }
                    return product.getPrice().multiply(product.getQuantity());
                }).reduce(Money.ZERO, Money::add);

        if(!totalAmount.equals(orderDetail.getTotalAmount())) {
            failureMessages.add("Price total is not correct for order " + orderApproval.getId());
        }
    }

    public void constructOrderApproval(OrderApprovalStatus orderApprovalStatus) {
        this.orderApproval = OrderApproval.builder()
                .orderApprovalId(new OrderApprovalId(UUID.randomUUID()))
                .restaurantId(getId())
                .orderId(orderDetail.getId())
                .orderApprovalStatus(orderApprovalStatus)
                .build();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private Restaurant(Builder builder) {
        super.setId(builder.restaurantId);
        orderApproval = builder.orderApproval;
        active = builder.active;
        orderDetail = builder.orderDetail;
    }

    public static Builder builder() {
        return new Builder();
    }

    public OrderApproval getOrderApproval() {
        return orderApproval;
    }

    public boolean isActive() {
        return active;
    }

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public static final class Builder implements github.alessandrofazio.domain.utils.Builder<Restaurant> {
        private RestaurantId restaurantId;
        private OrderApproval orderApproval;
        private boolean active;
        private OrderDetail orderDetail;
        @Override
        public Restaurant build() {
            return new Restaurant(this);
        }

        public Builder restaurantId(RestaurantId restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        public Builder orderApproval(OrderApproval orderApproval) {
            this.orderApproval = orderApproval;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder orderDetail(OrderDetail orderDetail) {
            this.orderDetail = orderDetail;
            return this;
        }
    }
}
