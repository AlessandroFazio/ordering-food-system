package github.alessandrofazio.customer.service.dataaccess.mapper;

import github.alessandrofazio.customer.service.dataaccess.entity.CustomerEntity;
import github.alessandrofazio.customer.service.domain.entity.Customer;
import github.alessandrofazio.customer.service.domain.entity.CustomerInformation;
import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.domain.valueobject.Username;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {
    public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        return Customer.builder()
                .customerId(new CustomerId(customerEntity.getId()))
                .username(new Username(customerEntity.getUsername()))
                .customerInformation(CustomerInformation.builder()
                        .firstName(customerEntity.getFirstName())
                        .firstName(customerEntity.getLastName())
                        .build())
                .build();
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
