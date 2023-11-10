package github.alessandrofazio.order.service.domain.entity;

import github.alessandrofazio.domain.entity.AggregateRoot;
import github.alessandrofazio.domain.entity.BaseEntity;
import github.alessandrofazio.domain.utils.Builder;
import github.alessandrofazio.domain.valueobject.RestaurantId;

import java.util.List;

public class Restaurant extends AggregateRoot<RestaurantId> {
    private final List<Product> products;
    private boolean active;

    private Restaurant(builder builder) {
        super.setId(builder.restaurantId);
        products = builder.products;
        active = builder.active;
    }

    public static builder builder() {
        return new builder();
    }

    public List<Product> getProducts() {
        return products;
    }

    public boolean isActive() {
        return active;
    }

    public static final class builder implements Builder<Restaurant> {
        private RestaurantId restaurantId;
        private List<Product> products;
        private boolean active;

        @Override
        public Restaurant build() {
            return new Restaurant(this);
        }

        public builder restaurantId(RestaurantId restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        public builder products(List<Product> products) {
            this.products = products;
            return this;
        }

        public builder active(boolean active) {
            this.active = active;
            return this;
        }
    }
}
