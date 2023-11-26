package github.alessandrofazio.customer.service.domain.mapper;

import github.alessandrofazio.customer.service.domain.create.CreateCustomerCommand;
import github.alessandrofazio.customer.service.domain.create.CreateCustomerResponse;
import github.alessandrofazio.customer.service.domain.entity.Customer;
import github.alessandrofazio.customer.service.domain.entity.CustomerInformation;
import github.alessandrofazio.customer.service.domain.event.CustomerCreatedEvent;
import github.alessandrofazio.domain.valueobject.CustomerId;
import github.alessandrofazio.domain.valueobject.Username;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static github.alessandrofazio.domain.constant.DomainConstants.UTC;

@Component
public class CustomerDataMapper {
    public Customer CustomerCreateRequestToCustomer(CreateCustomerCommand createCustomerCommand) {
        return Customer.builder()
                .customerId(new CustomerId(createCustomerCommand.getCustomerId()))
                .username(new Username(createCustomerCommand.getUsername()))
                .customerInformation(CustomerInformation.builder()
                        .firstName(createCustomerCommand.getFirstName())
                        .lastName(createCustomerCommand.getLastName())
                        .build())
                .build();
    }

    public CreateCustomerResponse customerTocreateCustomerResponse(Customer customer, String message) {
        return CreateCustomerResponse.builder()
                .customerId(customer.getId().getValue())
                .message(message)
                .build();
    }

    public CustomerCreatedEvent customerToCustomerCreatedEvent(Customer customer) {
        return new CustomerCreatedEvent(
                customer.getId(),
                customer.getUsername(),
                customer.getCustomerInformation(),
                ZonedDateTime.now(ZoneId.of(UTC)));
    }
}
