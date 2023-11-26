package github.alessandrofazio.customer.service.domain;

import github.alessandrofazio.customer.service.domain.create.CreateCustomerCommand;
import github.alessandrofazio.customer.service.domain.create.CreateCustomerResponse;
import github.alessandrofazio.customer.service.domain.entity.Customer;
import github.alessandrofazio.customer.service.domain.event.CustomerCreatedEvent;
import github.alessandrofazio.customer.service.domain.mapper.CustomerDataMapper;
import github.alessandrofazio.customer.service.domain.ports.input.CustomerApplicationService;
import github.alessandrofazio.customer.service.domain.ports.output.message.publisher.CustomerMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerApplicationServiceImpl implements CustomerApplicationService {
    public static final String SUCCESS_MESSAGE = "Customer saved successfully";
    
    private final CustomerCreateCommandHandler customerCreateCommandHandler;
    private final CustomerDataMapper customerDataMapper;
    private final CustomerMessagePublisher customerMessagePublisher;

    @Override
    public CreateCustomerResponse createCustomer(CreateCustomerCommand createCustomerCommand) {
        Customer customer =
                customerCreateCommandHandler.createCustomer(createCustomerCommand);
        customerMessagePublisher.publish(customerDataMapper.customerToCustomerCreatedEvent(customer));
        return customerDataMapper.customerTocreateCustomerResponse(customer, SUCCESS_MESSAGE);
    }
}
