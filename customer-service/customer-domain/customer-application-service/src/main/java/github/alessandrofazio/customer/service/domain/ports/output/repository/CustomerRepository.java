package github.alessandrofazio.customer.service.domain.ports.output.repository;

import github.alessandrofazio.customer.service.domain.entity.Customer;

public interface CustomerRepository {
    Customer createCustomer(Customer customer);
}
