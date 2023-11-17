package github.alessandrofazio.payment.service.domain.config;

import github.alessandrofazio.payment.service.domain.PaymentDomainService;
import github.alessandrofazio.payment.service.domain.PaymentDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public PaymentDomainService paymentDomainService() {
        return new PaymentDomainServiceImpl();
    }
}
