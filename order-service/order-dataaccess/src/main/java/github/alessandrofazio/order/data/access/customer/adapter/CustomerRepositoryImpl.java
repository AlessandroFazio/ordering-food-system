package github.alessandrofazio.order.data.access.customer.adapter;

import github.alessandrofazio.order.data.access.customer.mapper.CustomerDataAccessMapper;
import github.alessandrofazio.order.data.access.customer.repository.CustomerJpaRepository;
import github.alessandrofazio.order.service.domain.entity.Customer;
import github.alessandrofazio.service.domain.ports.output.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerDataAccessMapper customerDataAccessMapper;

    @Override
    public Optional<Customer> findCustomer(UUID customerId) {
        return customerJpaRepository.findById(customerId)
                .map(customerDataAccessMapper::customerEntityToCustomer);
    }

    @Override
    @Transactional
    public Customer save(Customer customer) {
        return customerDataAccessMapper.customerEntityToCustomer(
                customerJpaRepository.save(
                        customerDataAccessMapper.customerToCustomerEntity(customer)));
    }
}
