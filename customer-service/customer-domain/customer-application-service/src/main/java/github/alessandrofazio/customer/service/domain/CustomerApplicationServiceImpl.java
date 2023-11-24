package github.alessandrofazio.customer.service.domain;

import github.alessandrofazio.customer.service.domain.dto.CustomerCreateRequest;
import github.alessandrofazio.customer.service.domain.entity.Customer;
import github.alessandrofazio.customer.service.domain.exception.CustomerApplicationException;
import github.alessandrofazio.customer.service.domain.mapper.CustomerDataMapper;
import github.alessandrofazio.customer.service.domain.outbox.model.CustomerOutboxMessage;
import github.alessandrofazio.customer.service.domain.outbox.scheduler.CustomerOutboxHelper;
import github.alessandrofazio.customer.service.domain.ports.output.message.publisher.CustomerRequestMessagePublisher;
import github.alessandrofazio.customer.service.domain.ports.output.repository.CustomerRepository;
import github.alessandrofazio.domain.valueobject.Username;
import github.alessandrofazio.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerRequestHelper {

    private final CustomerDomainService customerDomainService;
    private final CustomerRepository customerRepository;
    private final CustomerDataMapper customerDataMapper;
    private final CustomerRequestMessagePublisher customerRequestMessagePublisher;

    @Transactional
    public void persistCustomer(CustomerCreateRequest customerCreateRequest) {
        Customer customer =
                customerDataMapper.CustomerCreateRequestToCustomer(customerCreateRequest);

        if(customerAlreadyExists(customer.getUsername())) {
            log.error("Customer with username: {} already exists", customer.getUsername().getValue());
            throw new CustomerApplicationException("Customer with username: "
                    + customer.getUsername().getValue() +  " already exists");
        }
        customerRepository.save(customer);
        log.info("Customer is saved with username: {} and customerId: {}",
                customer.getUsername().getValue(), customer.getId().getValue());
    }

    @Transactional(readOnly = true)
    public boolean customerAlreadyExists(Username username) {
        Optional<Customer> response = customerRepository.findByUsername(username);
        return response.isPresent();
    }
}
