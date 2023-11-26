package github.alessandrofazio.service.domain.saga;

import github.alessandrofazio.domain.valueobject.OrderStatus;
import github.alessandrofazio.domain.valueobject.PaymentStatus;
import github.alessandrofazio.order.service.domain.OrderDomainService;
import github.alessandrofazio.order.service.domain.entity.Order;
import github.alessandrofazio.order.service.domain.event.OrderPaidEvent;
import github.alessandrofazio.order.service.domain.exception.OrderDomainException;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.saga.SagaStatus;
import github.alessandrofazio.saga.SagaStep;
import github.alessandrofazio.service.domain.dto.message.PaymentResponse;
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
public class OrderPaymentSaga implements SagaStep<PaymentResponse> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    @Override
    @Transactional
    public void process(PaymentResponse paymentResponse) {
        Optional<OrderPaymentOutboxMessage> orderPaymentMessageResponse =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatuses(
                        UUID.fromString(paymentResponse.getSagaId()), SagaStatus.STARTED);

        if(orderPaymentMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already processed", paymentResponse.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentMessageResponse.get();

        OrderPaidEvent orderPaidEvent = completePaymentForOrder(paymentResponse);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(
                orderPaidEvent.getOrder().getOrderStatus());

        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(
                orderPaymentOutboxMessage, orderPaidEvent.getOrder().getOrderStatus(), sagaStatus));

        approvalOutboxHelper.saveApprovalOutboxMessage(
                orderDataMapper.orderPaidEventToOrderApprovalEventPayload(orderPaidEvent),
                orderPaidEvent.getOrder().getOrderStatus(),
                sagaStatus,
                OutboxStatus.STARTED,
                UUID.fromString(paymentResponse.getSagaId()));

        log.info("Order with id: {} is paid", orderPaidEvent.getOrder().getId().getValue());
    }

    @Override
    @Transactional
    public void rollback(PaymentResponse paymentResponse) {

        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxResponse =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatuses(
                        UUID.fromString(paymentResponse.getSagaId()),
                        getCurrentSagaStatus(paymentResponse.getPaymentStatus()));

        if(orderPaymentOutboxResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already rolled back", paymentResponse.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxResponse.get();

        Order order = rollbackPaymentForOrder(paymentResponse);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());

        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(orderPaymentOutboxMessage,
                order.getOrderStatus(), sagaStatus));

        if(paymentResponse.getPaymentStatus() == PaymentStatus.CANCELLED) {
            approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(
                    paymentResponse.getSagaId(), order.getOrderStatus(), sagaStatus));
        }

        log.info("Order with id: {} is cancelled", order.getId().getValue());

    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(
            String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
        Optional<OrderApprovalOutboxMessage> result =
                approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatuses(
                        UUID.fromString(sagaId), SagaStatus.COMPENSATING);

        if(result.isEmpty()) {
            throw new OrderDomainException("Approval outbox message could not be found in " +
                    SagaStatus.COMPENSATING.name() + " status");
        }
        OrderApprovalOutboxMessage orderApprovalOutboxMessage = result.get();
        orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        orderApprovalOutboxMessage.setOrderStatus(orderStatus);
        orderApprovalOutboxMessage.setSagaStatus(sagaStatus);
        return orderApprovalOutboxMessage;
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(
            OrderPaymentOutboxMessage orderPaymentOutboxMessage, OrderStatus orderStatus, SagaStatus sagaStatus) {
        orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
        orderPaymentOutboxMessage.setOrderStatus(orderStatus);
        return orderPaymentOutboxMessage;
    }

    private OrderPaidEvent completePaymentForOrder(PaymentResponse paymentResponse) {
        log.info("Completing payment for order with id: {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order);
        orderSagaHelper.saveOrder(order);
        return orderPaidEvent;
    }

    private SagaStatus[] getCurrentSagaStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case COMPLETED -> new SagaStatus[] {SagaStatus.STARTED};
            case CANCELLED -> new SagaStatus[] {SagaStatus.PROCESSING};
            case FAILED -> new SagaStatus[] {SagaStatus.STARTED, SagaStatus.PROCESSING};
        };
    }

    private Order rollbackPaymentForOrder(PaymentResponse paymentResponse) {
        log.info("Cancelling order with id: {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return order;
    }
}
