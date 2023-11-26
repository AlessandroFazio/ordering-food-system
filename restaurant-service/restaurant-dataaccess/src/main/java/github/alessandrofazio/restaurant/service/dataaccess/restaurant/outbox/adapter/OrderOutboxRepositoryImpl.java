package github.alessandrofazio.restaurant.service.dataaccess.restaurant.outbox.adapter;

import github.alessandrofazio.domain.valueobject.OrderApprovalStatus;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.restaurant.service.dataaccess.restaurant.outbox.entity.OrderOutboxEntity;
import github.alessandrofazio.restaurant.service.dataaccess.restaurant.outbox.exception.OrderOutboxNotFoundException;
import github.alessandrofazio.restaurant.service.dataaccess.restaurant.outbox.mapper.OrderOutboxDataMapper;
import github.alessandrofazio.restaurant.service.dataaccess.restaurant.outbox.repository.OrderOutboxJpaRepository;
import github.alessandrofazio.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import github.alessandrofazio.restaurant.service.domain.ports.output.repository.OrderOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {

    private final OrderOutboxJpaRepository orderOutboxJpaRepository;
    private final OrderOutboxDataMapper orderOutboxDataMapper;

    @Override
    public OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage) {
        return orderOutboxDataMapper.OrderOutboxEntityToOrderOutboxMessage(
                orderOutboxJpaRepository.save(
                        orderOutboxDataMapper.orderOutboxMessageToOrderOutboxEntity(orderOutboxMessage)));
    }

    @Override
    public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndOutboxStatus(
            String type, UUID sagaId, OutboxStatus outboxStatus) {
        return orderOutboxJpaRepository.findByTypeAndSagaIdAndOutboxStatus(type, sagaId, outboxStatus)
                .map(orderOutboxDataMapper::OrderOutboxEntityToOrderOutboxMessage);
    }

    @Override
    public List<OrderOutboxMessage> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {
        List<OrderOutboxEntity> orderOutboxEntities =
                orderOutboxJpaRepository.findByTypeAndOutboxStatus(type, outboxStatus);

        if(orderOutboxEntities.isEmpty()) {
            log.debug("Approval outbox object cannot be found for saga type: " + type);

        }
        return orderOutboxEntities.stream()
                .map(orderOutboxDataMapper::OrderOutboxEntityToOrderOutboxMessage)
                .toList();
    }

    @Override
    public void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {
        orderOutboxJpaRepository.deleteByTypeAndOutboxStatus(type, outboxStatus);
    }
}
