package github.alessandrofazio.order.service.domain.entity;

public class CustomerInformation {
    private final String firstName;
    private final String lastName;

    private CustomerInformation(Builder builder) {
        firstName = builder.firstName;
        lastName = builder.lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements github.alessandrofazio.domain.utils.Builder<CustomerInformation> {
        private String firstName;
        private String lastName;
        @Override
        public CustomerInformation build() {
            return new CustomerInformation(this);
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

    }
}
