package github.alessandrofazio.customer.service.domain.entity;

import github.alessandrofazio.domain.entity.AggregateRoot;
import github.alessandrofazio.domain.utils.Builder;
import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.domain.valueobject.Username;

public class Customer extends AggregateRoot<CustomerId> {
    private final Username username;
    private final CustomerInformation customerInformation;



    public Username getUsername() {
        return username;
    }

    public CustomerInformation getCustomerInformation() {
        return customerInformation;
    }

    private Customer(Builder builder) {
        super.setId(builder.customerId);
        username = builder.username;
        customerInformation = builder.customerInformation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements github.alessandrofazio.domain.utils.Builder<Customer> {
        private CustomerId customerId;
        private Username username;
        private CustomerInformation customerInformation;

        @Override
        public Customer build() {
            return new Customer(this);
        }

        public Builder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder customerInformation(CustomerInformation customerInformation) {
            this.customerInformation = customerInformation;
            return this;
        }

        public Builder username(Username username) {
            this.username = username;
            return this;
        }
    }
}
