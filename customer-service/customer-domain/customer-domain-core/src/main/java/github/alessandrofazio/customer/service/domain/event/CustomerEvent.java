package github.alessandrofazio.customer.service.domain.event;

import github.alessandrofazio.customer.service.domain.entity.Customer;
import github.alessandrofazio.customer.service.domain.entity.CustomerInformation;
import github.alessandrofazio.domain.event.DomainEvent;
import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.domain.valueobject.Username;

import java.time.ZonedDateTime;

public abstract class CustomerEvent implements DomainEvent<Customer> {
    private final CustomerId customerId;
    private final Username username;
    private final CustomerInformation customerInformation;
    private final ZonedDateTime createdAt;

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Username getUsername() {
        return username;
    }

    public CustomerInformation getCustomerInformation() {
        return customerInformation;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public CustomerEvent(CustomerId customerId, Username username, CustomerInformation customerInformation, ZonedDateTime createdAt) {
        this.customerId = customerId;
        this.username = username;
        this.customerInformation = customerInformation;
        this.createdAt = createdAt;
    }
}
