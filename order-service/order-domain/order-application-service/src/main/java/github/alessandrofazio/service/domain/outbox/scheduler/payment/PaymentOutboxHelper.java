package github.alessandrofazio.service.domain.outbox.scheduler.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.alessandrofazio.domain.valueobject.OrderStatus;
import github.alessandrofazio.order.service.domain.exception.OrderDomainException;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.saga.SagaStatus;
import github.alessandrofazio.domain.event.payload.OrderPaymentEventPayload;
import github.alessandrofazio.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import github.alessandrofazio.service.domain.ports.output.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static github.alessandrofazio.domain.constant.DomainConstants.UTC;
import static github.alessandrofazio.saga.order.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOutboxHelper {

    private final PaymentOutboxRepository paymentOutboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<OrderPaymentOutboxMessage> getPaymentOutboxMessageByOutboxStatusAndSagaStatuses(
            OutboxStatus outboxStatus, SagaStatus... sagaStatuses) {
        return paymentOutboxRepository.findByTypeAndOutboxStatusAndSagaStatuses(ORDER_SAGA_NAME, outboxStatus, sagaStatuses);
    }

    @Transactional(readOnly = true)
    public Optional<OrderPaymentOutboxMessage> getPaymentOutboxMessageBySagaIdAndSagaStatuses(
            UUID sagaId, SagaStatus... sagaStatuses) {
        return paymentOutboxRepository.findByTypeAndSagaIdAndSagaStatuses(ORDER_SAGA_NAME, sagaId, sagaStatuses);
    }

    @Transactional
    public void save(OrderPaymentOutboxMessage orderPaymentOutboxMessage) {
        OrderPaymentOutboxMessage response = paymentOutboxRepository.save(orderPaymentOutboxMessage);
        if(response == null) {
            log.error("Could not save " + orderPaymentOutboxMessage.getClass().getSimpleName() +
                    " with outbox id: " + orderPaymentOutboxMessage.getId());
            throw new OrderDomainException("Could not save " + orderPaymentOutboxMessage.getClass().getSimpleName() +
                    " with outbox id: " + orderPaymentOutboxMessage.getId());
        }
        log.info(orderPaymentOutboxMessage.getClass().getSimpleName() +
                " saved with outbox id: {}", orderPaymentOutboxMessage.getId());
    }

    @Transactional
    public void deletePaymentOutboxMessageByOutboxAndSagaStatuses(OutboxStatus outboxStatus,
                                                                  SagaStatus... sagaStatuses) {
        paymentOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatuses(
                ORDER_SAGA_NAME, outboxStatus, sagaStatuses);
    }

    @Transactional
    public void savePaymentOutboxMessage(OrderPaymentEventPayload orderPaymentEventPayload,
                                         OrderStatus orderStatus, SagaStatus sagaStatus,
                                         OutboxStatus outboxStatus, UUID sagaId) {
        save(OrderPaymentOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .createdAt(orderPaymentEventPayload.getCreatedAt())
                .processedAt(ZonedDateTime.now(ZoneId.of(UTC)))
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(orderPaymentEventPayload))
                .sagaStatus(sagaStatus)
                .outboxStatus(outboxStatus)
                .orderStatus(orderStatus)
                .build());
    }

    private String createPayload(OrderPaymentEventPayload orderPaymentEventPayload) {
        try {
            return objectMapper.writeValueAsString(orderPaymentEventPayload);
        } catch (JsonProcessingException e) {
            log.error("Could not create OrderPaymentEventPayload object for order id: {}",
                    orderPaymentEventPayload.getOrderId());
            throw new OrderDomainException("Could not create OrderPaymentEventPayload object for order id: " +
                    orderPaymentEventPayload.getOrderId());
        }
    }
}
