package github.alessandrofazio.service.domain.outbox.scheduler.approval;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.alessandrofazio.domain.valueobject.OrderStatus;
import github.alessandrofazio.order.service.domain.exception.OrderDomainException;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.saga.SagaStatus;
import github.alessandrofazio.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import github.alessandrofazio.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import github.alessandrofazio.service.domain.ports.output.repository.ApprovalOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
public class ApprovalOutboxHelper {

    private final ApprovalOutboxRepository approvalOutboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<OrderApprovalOutboxMessage> getOrderApprovalOutboxMessageByOutboxStatusAndSagaStatuses(
            OutboxStatus outboxStatus, SagaStatus... sagaStatuses) {
        return approvalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatuses(ORDER_SAGA_NAME, outboxStatus, sagaStatuses);
    }

    @Transactional(readOnly = true)
    public Optional<OrderApprovalOutboxMessage> getApprovalOutboxMessageBySagaIdAndSagaStatuses(
            UUID sagaId, SagaStatus... sagaStatuses) {
        return approvalOutboxRepository.findByTypeAndSagaIdAndSagaStatuses(ORDER_SAGA_NAME, sagaId, sagaStatuses);
    }

    @Transactional
    public void save(OrderApprovalOutboxMessage orderApprovalOutboxMessage) {
        OrderApprovalOutboxMessage response = approvalOutboxRepository
                .save(orderApprovalOutboxMessage);
        if(response == null) {
            log.error("Could not save {} with outbox id: {}", orderApprovalOutboxMessage.getClass().getSimpleName(),
                    orderApprovalOutboxMessage.getId());
            throw new OrderDomainException("Could not save " + orderApprovalOutboxMessage.getClass().getSimpleName() +
                    " with outbox id: " +  orderApprovalOutboxMessage.getId());
        }
        log.info("{} saved with outbox id: {}", orderApprovalOutboxMessage.getClass().getSimpleName(),
                orderApprovalOutboxMessage.getId());
    }

    @Transactional
    public void deleteApprovalOutboxMessageByOutboxStatusAndSagaStatuses(
            OutboxStatus outboxStatus, SagaStatus... sagaStatuses) {
        approvalOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatuses(
                ORDER_SAGA_NAME, outboxStatus, sagaStatuses);
    }

    @Transactional
    public void saveApprovalOutboxMessage(OrderApprovalEventPayload orderApprovalEventPayload,
                                          OrderStatus orderStatus,
                                          SagaStatus sagaStatus,
                                          OutboxStatus outboxStatus,
                                          UUID sagaId) {
        save(OrderApprovalOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .createdAt(orderApprovalEventPayload.getCreatedAt())
                .processedAt(ZonedDateTime.now(ZoneId.of(UTC)))
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(orderApprovalEventPayload))
                .orderStatus(orderStatus)
                .sagaStatus(sagaStatus)
                .outboxStatus(outboxStatus)
                .build());
    }

    private String createPayload(OrderApprovalEventPayload orderApprovalEventPayload) {
        try {
            return objectMapper.writeValueAsString(orderApprovalEventPayload);
        } catch (JsonProcessingException e) {
            log.error("Could not create OrderApprovalEventPayload for order with id: {}",
                    orderApprovalEventPayload.getOrderId());
            throw new OrderDomainException("Could not create OrderApprovalEventPayload for order with id: " +
                    orderApprovalEventPayload.getOrderId());
        }
    }
}
