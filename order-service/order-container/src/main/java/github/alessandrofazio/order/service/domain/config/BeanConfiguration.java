package github.alessandrofazio.order.service.domain.config;

import github.alessandrofazio.order.service.domain.OrderDomainService;
import github.alessandrofazio.order.service.domain.OrderDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }
}
