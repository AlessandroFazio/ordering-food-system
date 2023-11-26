package github.alessandrofazio.customer.service.domain;

import github.alessandrofazio.customer.service.domain.entity.Customer;
import github.alessandrofazio.customer.service.domain.event.CustomerCreatedEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
public class CustomerDomainServiceImpl implements CustomerDomainService {
    @Override
    public CustomerCreatedEvent validateAndInitiateCustomer(Customer customer) {
        log.info("Customer with id: {} is initiated", customer.getId().getValue());
        return new CustomerCreatedEvent(
                customer.getId(), customer.getUsername(), customer.getCustomerInformation(), ZonedDateTime.now(ZoneId.of("UTC")));
    }
}
