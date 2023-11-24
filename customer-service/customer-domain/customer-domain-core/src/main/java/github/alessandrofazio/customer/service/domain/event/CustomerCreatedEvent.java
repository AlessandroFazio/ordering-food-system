package github.alessandrofazio.customer.service.domain.event;

import github.alessandrofazio.customer.service.domain.entity.CustomerInformation;
import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.domain.valueobject.Username;

import java.time.ZonedDateTime;

public class CustomerCreatedEvent extends CustomerEvent {
    public CustomerCreatedEvent(CustomerId customerId, Username username, CustomerInformation customerInformation, ZonedDateTime createdAt) {
        super(customerId, username, customerInformation, createdAt);
    }
}
