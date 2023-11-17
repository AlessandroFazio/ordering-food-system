package github.alessandrofazio.service.domain;

import github.alessandrofazio.order.service.domain.OrderDomainService;
import github.alessandrofazio.order.service.domain.OrderDomainServiceImpl;
import github.alessandrofazio.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import github.alessandrofazio.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import github.alessandrofazio.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import github.alessandrofazio.service.domain.ports.output.repository.CustomerRepository;
import github.alessandrofazio.service.domain.ports.output.repository.OrderRepository;
import github.alessandrofazio.service.domain.ports.output.repository.RestaurantRepository;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "github.alessandrofazio")
public class OrderTestConfiguration {

    @Bean
    public OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher() {
        return Mockito.mock(OrderCreatedPaymentRequestMessagePublisher.class);
    }

    @Bean
    public OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher() {
        return Mockito.mock(OrderCancelledPaymentRequestMessagePublisher.class);
    }

    @Bean
    public OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher() {
        return Mockito.mock(OrderPaidRestaurantRequestMessagePublisher.class);
    }

    @Bean
    public OrderRepository orderRepository() {
        return Mockito.mock(OrderRepository.class);
    }

    @Bean
    public CustomerRepository customerRepository() {
        return Mockito.mock(CustomerRepository.class);
    }

    @Bean
    public RestaurantRepository restaurantRepository() {
        return Mockito.mock(RestaurantRepository.class);
    }

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }
}
