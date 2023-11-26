package github.alessandrofazio.customer.service.domain;

import github.alessandrofazio.customer.service.domain.create.CreateCustomerCommand;
import github.alessandrofazio.customer.service.domain.entity.Customer;
import github.alessandrofazio.customer.service.domain.event.CustomerCreatedEvent;
import github.alessandrofazio.customer.service.domain.exception.CustomerDomainException;
import github.alessandrofazio.customer.service.domain.mapper.CustomerDataMapper;
import github.alessandrofazio.customer.service.domain.ports.output.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerCreateCommandHandler {

    private final CustomerDomainService customerDomainService;
    private final CustomerRepository customerRepository;
    private final CustomerDataMapper customerDataMapper;

    @Transactional
    public Customer createCustomer(CreateCustomerCommand createCustomerCommand) {
        Customer customer = customerDataMapper.CustomerCreateRequestToCustomer(createCustomerCommand);
        customerDomainService.validateAndInitiateCustomer(customer);
        Customer savedCustomer = customerRepository.createCustomer(customer);
        if(savedCustomer == null) {
            log.error("Could not save customer with id: {}", createCustomerCommand.getCustomerId().toString());
            throw new CustomerDomainException(
                    "Could not save customer with id: " + createCustomerCommand.getCustomerId().toString());
        }
        log.info("Returning CustomerCreatedEvent for customer id: {}",
                createCustomerCommand.getCustomerId().toString());
        return customer;
    }
}
