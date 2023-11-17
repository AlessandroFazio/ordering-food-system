package github.alessandrofazio.restaurant.service.domain.entity;

import github.alessandrofazio.domain.entity.BaseEntity;
import github.alessandrofazio.domain.utils.Builder;
import github.alessandrofazio.domain.valueobject.Money;
import github.alessandrofazio.domain.valueobject.ProductId;

public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;
    private final int quantity;
    private boolean available;

    public void updateWithConfirmedNamePriceAndAvailability(String name, Money price, boolean available) {
        this.name = name;
        this.price = price;
        this.available = available;
    }

    private Product(Builder builder) {
        super.setId(builder.productId);
        name = builder.name;
        price = builder.price;
        quantity = builder.quantity;
        available = builder.available;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isAvailable() {
        return available;
    }

    public static final class Builder implements github.alessandrofazio.domain.utils.Builder<Product> {
        private ProductId productId;
        private String name;
        private Money price;
        private int quantity;
        private boolean available;
        @Override
        public Product build() {
            return new Product(this);
        }

        public Builder productId(ProductId productId) {
            this.productId = productId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(Money price) {
            this.price = price;
            return this;
        }


        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder available(boolean available) {
            this.available = available;
            return this;
        }
    }
}
