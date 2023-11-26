package github.alessandrofazio.service.domain.ports.input.message.listener.customer;

import github.alessandrofazio.service.domain.dto.message.CustomerModel;

public interface CustomerMessageListener {

    void customerCreated(CustomerModel customerModel);
}
