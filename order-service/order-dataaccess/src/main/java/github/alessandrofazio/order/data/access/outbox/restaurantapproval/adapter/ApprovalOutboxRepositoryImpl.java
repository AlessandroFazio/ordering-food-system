package github.alessandrofazio.order.data.access.outbox.restaurantapproval.adapter;

import github.alessandrofazio.order.data.access.outbox.restaurantapproval.entity.ApprovalOutboxEntity;
import github.alessandrofazio.order.data.access.outbox.restaurantapproval.exception.ApprovalOutboxNotFoundException;
import github.alessandrofazio.order.data.access.outbox.restaurantapproval.mapper.ApprovalOutboxDataMapper;
import github.alessandrofazio.order.data.access.outbox.restaurantapproval.repository.ApprovalOutboxJpaRepository;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.saga.SagaStatus;
import github.alessandrofazio.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import github.alessandrofazio.service.domain.ports.output.repository.ApprovalOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalOutboxRepositoryImpl implements ApprovalOutboxRepository {

    private final ApprovalOutboxJpaRepository approvalOutboxJpaRepository;
    private final ApprovalOutboxDataMapper approvalOutboxDataMapper;
    @Override
    public OrderApprovalOutboxMessage save(OrderApprovalOutboxMessage orderPaymentOutboxMessage) {
        return approvalOutboxDataMapper.approvalOutboxEntityToOrderApprovalOutboxMessage(
                approvalOutboxJpaRepository.save(
                        approvalOutboxDataMapper.orderApprovalOutboxMessageToApprovalOutboxEntity(
                                orderPaymentOutboxMessage)));
    }

    @Override
    public List<OrderApprovalOutboxMessage> findByTypeAndOutboxStatusAndSagaStatuses(
            String type, OutboxStatus outboxStatus, SagaStatus... sagaStatuses) {
        List<ApprovalOutboxEntity> approvalOutboxEntities =
                approvalOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(
                        type, outboxStatus, Arrays.asList(sagaStatuses));

        if(approvalOutboxEntities.isEmpty()) {
            log.debug("Cannot find ApprovalOutboxEntity for saga type: {}", type);
        }

        return approvalOutboxEntities.stream()
                .map(approvalOutboxDataMapper::approvalOutboxEntityToOrderApprovalOutboxMessage)
                .toList();
    }

    @Override
    public Optional<OrderApprovalOutboxMessage> findByTypeAndSagaIdAndSagaStatuses(
            String type, UUID sagaId, SagaStatus... sagaStatuses) {
        return approvalOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(
                type, sagaId, Arrays.asList(sagaStatuses))
                .map(approvalOutboxDataMapper::approvalOutboxEntityToOrderApprovalOutboxMessage);
    }

    @Override
    public void deleteByTypeAndOutboxStatusAndSagaStatuses(
            String type, OutboxStatus outboxStatus, SagaStatus... sagaStatuses) {
        approvalOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(
                type, outboxStatus, Arrays.asList(sagaStatuses));
    }
}
