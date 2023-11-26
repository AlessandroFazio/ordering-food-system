package github.alessandrofazio.service.application.rest;

import github.alessandrofazio.customer.service.domain.create.CreateCustomerCommand;
import github.alessandrofazio.customer.service.domain.create.CreateCustomerResponse;
import github.alessandrofazio.customer.service.domain.ports.input.CustomerApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/customers", produces = "application/vnd.api.v1+json")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerApplicationService customerApplicationService;

    @PostMapping
    public ResponseEntity<CreateCustomerResponse> createCustomer(
            @RequestBody CreateCustomerCommand createCustomerCommand) {
        log.info("Creating customer with username: {}", createCustomerCommand.getUsername());
        CreateCustomerResponse createCustomerResponse =
                customerApplicationService.createCustomer(createCustomerCommand);
        return ResponseEntity.ok(createCustomerResponse);
    }

}
