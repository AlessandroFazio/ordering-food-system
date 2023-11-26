package github.alessandrofazio.payment.service.domain.outbox.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.alessandrofazio.domain.valueobject.PaymentStatus;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.payment.service.domain.exception.PaymentDomainException;
import github.alessandrofazio.payment.service.domain.outbox.model.OrderEventPayload;
import github.alessandrofazio.payment.service.domain.outbox.model.OrderOutboxMessage;
import github.alessandrofazio.payment.service.domain.ports.output.repository.OrderOutboxRepository;
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
public class OrderOutboxHelper {

    private final OrderOutboxRepository orderOutboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Optional<OrderOutboxMessage> getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(
            UUID sagaId, PaymentStatus paymentStatus) {
        return orderOutboxRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
                ORDER_SAGA_NAME, sagaId, paymentStatus, OutboxStatus.COMPLETED);
    }

    @Transactional(readOnly = true)
    public List<OrderOutboxMessage> getOrderOutboxMessageByOutboxStatus(OutboxStatus outboxStatus) {
        return orderOutboxRepository.findByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
    }

    @Transactional
    public void deleteOrderOutboxMessageByOutboxStatus(OutboxStatus outboxStatus) {
        orderOutboxRepository.deleteByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
    }

    public void saveOrderOutboxMessage(
            OrderEventPayload orderEventPayload, PaymentStatus paymentStatus, OutboxStatus outboxStatus, UUID sagaId) {
        save(OrderOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(orderEventPayload))
                .createdAt(orderEventPayload.getCreatedAt())
                .processedAt(ZonedDateTime.now(ZoneId.of(UTC)))
                .paymentStatus(paymentStatus)
                .outboxStatus(outboxStatus)
                .build());
    }

    private void save(OrderOutboxMessage orderOutboxMessage) {
        OrderOutboxMessage response = orderOutboxRepository.save(orderOutboxMessage);
        if(response == null) {
            log.error("Could not save OrderOutboxMessage");
            throw new PaymentDomainException("Could not save OrderOutboxMessage");
        }
        log.info("OrderOutboxMessage is saved with id: {}", orderOutboxMessage.getId());
    }

    private String createPayload(OrderEventPayload orderEventPayload) {
        try {
            return objectMapper.writeValueAsString(orderEventPayload);
        } catch (JsonProcessingException e) {
            log.error("Could not create OrderEventPayload", e);
            throw new PaymentDomainException("Could not create OrderEventPayload", e);
        }
    }
}
