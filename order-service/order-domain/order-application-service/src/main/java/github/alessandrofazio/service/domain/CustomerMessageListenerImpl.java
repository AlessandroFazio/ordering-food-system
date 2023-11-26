package github.alessandrofazio.service.domain;

import github.alessandrofazio.order.service.domain.entity.Customer;
import github.alessandrofazio.order.service.domain.exception.OrderDomainException;
import github.alessandrofazio.service.domain.dto.message.CustomerModel;
import github.alessandrofazio.service.domain.mapper.OrderDataMapper;
import github.alessandrofazio.service.domain.ports.input.message.listener.customer.CustomerMessageListener;
import github.alessandrofazio.service.domain.ports.output.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerMessageListenerImpl implements CustomerMessageListener {

    private final CustomerRepository customerRepository;
    private final OrderDataMapper orderDataMapper;

    @Override
    public void customerCreated(CustomerModel customerModel) {
        Customer savedCustomer = customerRepository.save(
                orderDataMapper.CustomerModelToCustomer(customerModel));
        if(savedCustomer == null) {
            log.error("Customer could not be created in order database with id: {}", customerModel.getId());
            throw new OrderDomainException("Customer could not be created in order database with id " + customerModel.getId());
        }

        log.info("Customer is created in order database with id: {}", customerModel.getId());
    }
}
