package github.alessandrofazio.restaurant.service.domain.config;

import github.alessandrofazio.restaurant.service.domain.RestaurantDomainService;
import github.alessandrofazio.restaurant.service.domain.RestaurantDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public RestaurantDomainService restaurantDomainService() {
        return new RestaurantDomainServiceImpl();
    }
}
