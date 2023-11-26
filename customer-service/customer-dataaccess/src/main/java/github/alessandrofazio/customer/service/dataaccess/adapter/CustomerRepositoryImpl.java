package github.alessandrofazio.customer.service.dataaccess.adapter;

import github.alessandrofazio.customer.service.dataaccess.mapper.CustomerDataAccessMapper;
import github.alessandrofazio.customer.service.dataaccess.repository.CustomerJpaRepository;
import github.alessandrofazio.customer.service.domain.entity.Customer;
import github.alessandrofazio.customer.service.domain.ports.output.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerDataAccessMapper customerDataAccessMapper;

    @Override
    public Customer createCustomer(Customer customer) {
        return customerDataAccessMapper.customerEntityToCustomer(
                customerJpaRepository.save(
                        customerDataAccessMapper.customerToCustomerEntity(customer)));
    }
}
