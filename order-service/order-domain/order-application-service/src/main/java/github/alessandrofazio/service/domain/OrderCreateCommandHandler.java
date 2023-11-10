package github.alessandrofazio.service.domain;

import github.alessandrofazio.order.service.domain.OrderDomainService;
import github.alessandrofazio.order.service.domain.entity.Customer;
import github.alessandrofazio.order.service.domain.entity.Order;
import github.alessandrofazio.order.service.domain.entity.Restaurant;
import github.alessandrofazio.order.service.domain.event.OrderCreatedEvent;
import github.alessandrofazio.order.service.domain.exception.OrderDomainException;
import github.alessandrofazio.service.domain.dto.create.CreateOrderCommand;
import github.alessandrofazio.service.domain.dto.create.CreateOrderResponse;
import github.alessandrofazio.service.domain.mapper.OrderDataMapper;
import github.alessandrofazio.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import github.alessandrofazio.service.domain.ports.output.repository.CustomerRepository;
import github.alessandrofazio.service.domain.ports.output.repository.OrderRepository;
import github.alessandrofazio.service.domain.ports.output.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreateCommandHandler {

    private final OrderDataMapper orderDataMapper;
    private final OrderCreateHelper orderCreateHelper;
    private final OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher;

    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
        log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
        orderCreatedPaymentRequestMessagePublisher.publish(orderCreatedEvent);
        return orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder());
    }
}
