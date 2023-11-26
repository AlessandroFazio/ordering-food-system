package github.alessandrofazio.customer.service.domain.ports.input;

import github.alessandrofazio.customer.service.domain.create.CreateCustomerCommand;
import github.alessandrofazio.customer.service.domain.create.CreateCustomerResponse;
import jakarta.validation.Valid;

public interface CustomerApplicationService {
    CreateCustomerResponse createCustomer(@Valid CreateCustomerCommand createCustomerCommand);
}
