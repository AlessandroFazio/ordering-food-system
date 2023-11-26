package github.alessandrofazio.order.service.domain.entity;

import github.alessandrofazio.domain.entity.AggregateRoot;
import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.domain.valueobject.Username;

public class Customer extends AggregateRoot<CustomerId> {
    private Username username;
    private CustomerInformation customerInformation;

    public Customer(CustomerId customerId) {
        super.setId(customerId);
    }

    public Customer() {
    }

    public Customer(CustomerId customerId, Username username, CustomerInformation customerInformation) {
        super.setId(customerId);
        this.username = username;
        this.customerInformation = customerInformation;
    }

    public Username getUsername() {
        return username;
    }

    public CustomerInformation getCustomerInformation() {
        return customerInformation;
    }
}
