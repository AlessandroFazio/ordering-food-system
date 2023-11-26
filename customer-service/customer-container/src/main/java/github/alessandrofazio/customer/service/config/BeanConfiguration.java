package github.alessandrofazio.customer.service.config;

import github.alessandrofazio.customer.service.domain.CustomerDomainService;
import github.alessandrofazio.customer.service.domain.CustomerDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    public CustomerDomainService customerDomainService() {
        return new CustomerDomainServiceImpl();
    }
}
