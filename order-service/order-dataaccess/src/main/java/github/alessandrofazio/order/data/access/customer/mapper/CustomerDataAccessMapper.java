package github.alessandrofazio.order.data.access.customer.mapper;

import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.order.data.access.customer.entity.CustomerEntity;
import github.alessandrofazio.order.service.domain.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {

    public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        return new Customer(new CustomerId(customerEntity.getId()));
    }

    public CustomerEntity customerToCustomerEntity(Customer customer) {
        return CustomerEntity.builder()
                .id(customer.getId().getValue())
                .username(customer.getUsername().getValue())
                .firstName(customer.getCustomerInformation().getFirstName())
                .lastName(customer.getCustomerInformation().getLastName())
                .build();
    }
}
