package github.alessandrofazio.order.service.domain.entity;

import github.alessandrofazio.domain.entity.AggregateRoot;
import github.alessandrofazio.domain.utils.Builder;
import github.alessandrofazio.domain.valueobject.*;
import github.alessandrofazio.order.service.domain.exception.OrderDomainException;
import github.alessandrofazio.order.service.domain.valueobject.OrderItemId;
import github.alessandrofazio.order.service.domain.valueobject.StreetAddress;
import github.alessandrofazio.order.service.domain.valueobject.TrackingId;

import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {
    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final StreetAddress deliveryAddress;
    private final Money price;
    private final List<OrderItem> items;

    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessages;

    public CustomerId getCustomerId() {
        return customerId;
    }

    public void initializeOrder() {
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();
    }

    public void validateOrder() {
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    public void pay() {
        if(!orderStatus.equals(OrderStatus.PENDING)) throw new OrderDomainException(
                "Order is not in correct state for pay operation"
        );
        orderStatus = OrderStatus.PAID;

    }

    public void approve() {
        if(!orderStatus.equals(OrderStatus.PAID)) throw new OrderDomainException(
                "Order is not in correct state approve operation"
        );
        orderStatus = OrderStatus.APPROVED;
    }

    public void initCancel(List<String> failureMessages) {
        if(!orderStatus.equals(OrderStatus.PAID)) throw new OrderDomainException(
                "Order is not in correct state for initCancel operation"
        );
        orderStatus = OrderStatus.CANCELLING;
        updateFailureMessages(failureMessages);
    }

    public void cancel(List<String> failureMessages) {
        if(!(orderStatus == OrderStatus.CANCELLING || orderStatus == OrderStatus.PENDING)) throw new OrderDomainException(
                "Order is not in correct state for cancel operation"
        );
        orderStatus = OrderStatus.CANCELLED;
    }

    private void updateFailureMessages(List<String> failureMessages) {
        if(this.failureMessages != null && failureMessages != null) {
            this.failureMessages.addAll(
                    failureMessages.stream().filter(m -> !m.isEmpty()).toList());
        }
        if(this.failureMessages == null) {
            this.failureMessages = failureMessages;
        }
    }

    private void validateInitialOrder() {
        if(orderStatus != null || getId() != null) throw new OrderDomainException(
                "Order is not in correct state for initialization");
    }

    private void validateTotalPrice() {
        if(price == null || !price.isGreaterThanZero()) throw new OrderDomainException(
                "Total price must be greater than zero"
        );
    }

    private void validateItemsPrice() {
        Money orderItemsTotal = items.stream().map(orderItem -> {
           validateItemPrice(orderItem);
           return orderItem.getSubTotal();
        }).reduce(Money.ZERO, Money::add);

        if(!price.equals(orderItemsTotal)) throw new OrderDomainException(
                "Total price: " + price.getAmount() + " is not equal to Order items total: " + orderItemsTotal.getAmount()
        );
    }

    private void validateItemPrice(OrderItem orderItem) {
        if(!orderItem.isPriceValid()) throw new OrderDomainException(
                "Order item price: " + orderItem.getPrice().getAmount() +
                        " is not valid for product " + orderItem.getProduct().getId().getValue()
        );
    }

    private void initializeOrderItems() {
        long itemId = 1;
        for(OrderItem item: items) {
            item.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
        }
    }

    private Order(builder builder) {
        super.setId(builder.orderId);
        customerId = builder.customerId;
        restaurantId = builder.restaurantId;
        deliveryAddress = builder.deliveryAddress;
        price = builder.price;
        items = builder.items;
        trackingId = builder.trackingId;
        orderStatus = builder.orderStatus;
        failureMessages = builder.failureMessages;
    }

    public static builder builder() {
        return new builder();
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public StreetAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public static final class builder implements Builder<Order> {
        private OrderId orderId;
        private CustomerId customerId;
        private RestaurantId restaurantId;
        private StreetAddress deliveryAddress;
        private Money price;
        private List<OrderItem> items;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private List<String> failureMessages;

        @Override
        public Order build() {
            return new Order(this);
        }

        public builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public builder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public builder restaurantId(RestaurantId restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }
        public builder deliveryAddress(StreetAddress deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            return this;
        }
        public builder price(Money price) {
            this.price = price;
            return this;
        }

        public builder items(List<OrderItem> items) {
            this.items = items;
            return this;
        }

        public builder trackingId(TrackingId trackingId) {
            this.trackingId = trackingId;
            return this;
        }

        public builder orderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public builder failureMessages(List<String> failureMessages) {
            this.failureMessages = failureMessages;
            return this;
        }
    }
}
