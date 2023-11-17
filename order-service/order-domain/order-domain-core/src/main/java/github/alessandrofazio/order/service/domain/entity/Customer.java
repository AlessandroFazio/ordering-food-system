package github.alessandrofazio.order.service.domain.entity;

import github.alessandrofazio.domain.entity.AggregateRoot;
import github.alessandrofazio.domain.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {
    public Customer(CustomerId customerId) {
        super.setId(customerId);
    }

    public Customer() {
    }
}
