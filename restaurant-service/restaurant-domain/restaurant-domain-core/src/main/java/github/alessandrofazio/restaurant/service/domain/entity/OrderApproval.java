package github.alessandrofazio.restaurant.service.domain.entity;

import github.alessandrofazio.domain.entity.BaseEntity;
import github.alessandrofazio.domain.utils.Builder;
import github.alessandrofazio.domain.valueobject.OrderApprovalStatus;
import github.alessandrofazio.domain.valueobject.OrderId;
import github.alessandrofazio.domain.valueobject.RestaurantId;
import github.alessandrofazio.restaurant.service.domain.valueobject.OrderApprovalId;

import java.util.function.Predicate;

public class OrderApproval extends BaseEntity<OrderApprovalId> {
    private final RestaurantId restaurantId;
    private final OrderId orderId;
    private final OrderApprovalStatus orderApprovalStatus;

    private OrderApproval(Builder builder) {
        super.setId(builder.orderApprovalId);
        restaurantId = builder.restaurantId;
        orderId = builder.orderId;
        orderApprovalStatus = builder.orderApprovalStatus;
    }

    public static Builder builder() {
        return new Builder();
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public OrderApprovalStatus getOrderApprovalStatus() {
        return orderApprovalStatus;
    }

    public static final class Builder implements github.alessandrofazio.domain.utils.Builder<OrderApproval> {
        private OrderApprovalId orderApprovalId;
        private RestaurantId restaurantId;
        private OrderId orderId;
        private OrderApprovalStatus orderApprovalStatus;
        @Override
        public OrderApproval build() {
            return new OrderApproval(this);
        }

        public Builder orderApprovalId(OrderApprovalId orderApprovalId) {
            this.orderApprovalId = orderApprovalId;
            return this;
        }

        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder restaurantId(RestaurantId restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        public Builder orderApprovalStatus(OrderApprovalStatus orderApprovalStatus) {
            this.orderApprovalStatus = orderApprovalStatus;
            return this;
        }
    }
}
