package github.alessandrofazio.service.domain;

import github.alessandrofazio.domain.valueobject.OrderStatus;
import github.alessandrofazio.order.service.domain.event.OrderCreatedEvent;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.saga.SagaStatus;
import github.alessandrofazio.service.domain.dto.create.CreateOrderCommand;
import github.alessandrofazio.service.domain.dto.create.CreateOrderResponse;
import github.alessandrofazio.service.domain.mapper.OrderDataMapper;
import github.alessandrofazio.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import github.alessandrofazio.service.domain.saga.OrderSagaHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreateCommandHandler {

    private final OrderDataMapper orderDataMapper;
    private final OrderCreateHelper orderCreateHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final OrderSagaHelper orderSagaHelper;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
        log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
        CreateOrderResponse createOrderResponse = orderDataMapper.orderToCreateOrderResponse(
                orderCreatedEvent.getOrder(),"Order created successfully");

        paymentOutboxHelper.savePaymentOutboxMessage(
                orderDataMapper.orderCreatedEventToOrderPaymentEventPayload(orderCreatedEvent),
                orderCreatedEvent.getOrder().getOrderStatus(),
                orderSagaHelper.orderStatusToSagaStatus(orderCreatedEvent.getOrder().getOrderStatus()),
                OutboxStatus.STARTED,
                UUID.randomUUID());

        log.info("Returning CreateOrderResponse with order id: {}", orderCreatedEvent.getOrder().getId().getValue());

        return createOrderResponse;
    }
}
