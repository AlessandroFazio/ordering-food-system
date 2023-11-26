package github.alessandrofazio.service.domain.saga;

import github.alessandrofazio.domain.valueobject.OrderStatus;
import github.alessandrofazio.order.service.domain.OrderDomainService;
import github.alessandrofazio.order.service.domain.entity.Order;
import github.alessandrofazio.order.service.domain.event.OrderCancelledEvent;
import github.alessandrofazio.order.service.domain.exception.OrderDomainException;
import github.alessandrofazio.saga.SagaStatus;
import github.alessandrofazio.saga.SagaStep;
import github.alessandrofazio.service.domain.dto.message.RestaurantApprovalResponse;
import github.alessandrofazio.service.domain.mapper.OrderDataMapper;
import github.alessandrofazio.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import github.alessandrofazio.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import github.alessandrofazio.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import github.alessandrofazio.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static github.alessandrofazio.domain.constant.DomainConstants.UTC;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    @Override
    @Transactional
    public void process(RestaurantApprovalResponse restaurantApprovalResponse) {
        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxResponse =
                approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatuses(
                        UUID.fromString(restaurantApprovalResponse.getSagaId()), SagaStatus.PROCESSING);

        if(orderApprovalOutboxResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already processed",
                    restaurantApprovalResponse.getSagaId());
            return;
        }

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxResponse.get();
        Order order = approveOrder(restaurantApprovalResponse);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(
                order.getOrderStatus());

        approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(
                orderApprovalOutboxMessage, order.getOrderStatus(), sagaStatus));

        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(
                restaurantApprovalResponse.getSagaId(), order.getOrderStatus(), sagaStatus));

        log.info("Order with id: {} is approved", order.getId().getValue());
    }

    @Override
    @Transactional
    public void rollback(RestaurantApprovalResponse restaurantApprovalResponse) {

        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxResponse =
                approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatuses(
                        UUID.fromString(restaurantApprovalResponse.getSagaId()), SagaStatus.PROCESSING);

        if(orderApprovalOutboxResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already processed",
                    restaurantApprovalResponse.getSagaId());
            return;
        }

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxResponse.get();

        OrderCancelledEvent orderCancelledEvent = rollbackOrder(restaurantApprovalResponse);
        Order order = orderCancelledEvent.getOrder();

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());

        OrderApprovalOutboxMessage testApprovalMessage = getUpdatedApprovalOutboxMessage(
                orderApprovalOutboxMessage, order.getOrderStatus(), sagaStatus);
        approvalOutboxHelper.save(testApprovalMessage);

        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(
                restaurantApprovalResponse.getSagaId(),
                order.getOrderStatus(), sagaStatus));

        log.info("Order with id: {} is cancelled", order.getId().getValue());
    }

    private OrderCancelledEvent rollbackOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Cancelling order with id: {}", restaurantApprovalResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelOrderPayment(order, restaurantApprovalResponse.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return orderCancelledEvent;
    }

    private Order approveOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Approving order with id: {}", restaurantApprovalResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        orderDomainService.approveOrder(order);
        orderSagaHelper.saveOrder(order);
        return order;
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(
            OrderApprovalOutboxMessage orderApprovalOutboxMessage, OrderStatus orderStatus, SagaStatus sagaStatus) {
        orderApprovalOutboxMessage.setOrderStatus(orderStatus);
        orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        orderApprovalOutboxMessage.setSagaStatus(sagaStatus);
        return orderApprovalOutboxMessage;
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(
            String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxResponse =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatuses(
                        UUID.fromString(sagaId), SagaStatus.PROCESSING);

        if(orderPaymentOutboxResponse.isEmpty()) {
            log.error("Could not found order with saga id: {} in outbox table",  sagaId);
            throw new OrderDomainException("Could not found order with saga id: " + sagaId + " in outbox table");
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxResponse.get();
        orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
        orderPaymentOutboxMessage.setOrderStatus(orderStatus);
        orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        return orderPaymentOutboxMessage;
    }

}
