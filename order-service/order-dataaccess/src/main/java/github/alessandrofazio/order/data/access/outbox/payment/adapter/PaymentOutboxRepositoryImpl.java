package github.alessandrofazio.order.data.access.outbox.payment.adapter;

import github.alessandrofazio.order.data.access.outbox.payment.entity.PaymentOutboxEntity;
import github.alessandrofazio.order.data.access.outbox.payment.exception.PaymentOutboxNotFoundException;
import github.alessandrofazio.order.data.access.outbox.payment.mapper.PaymentOutboxDataMapper;
import github.alessandrofazio.order.data.access.outbox.payment.repository.PaymentOutboxJpaRepository;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.saga.SagaStatus;
import github.alessandrofazio.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import github.alessandrofazio.service.domain.ports.output.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository {

    private final PaymentOutboxJpaRepository paymentOutboxJpaRepository;
    private final PaymentOutboxDataMapper paymentOutboxDataMapper;

    @Override
    public OrderPaymentOutboxMessage save(OrderPaymentOutboxMessage orderPaymentOutboxMessage) {
        return paymentOutboxDataMapper.paymentOutboxEntityToOrderPaymentOutboxMessage(
                paymentOutboxJpaRepository.save(
                        paymentOutboxDataMapper.orderPaymentOutboxMessageToPaymentOutboxEntity(
                                orderPaymentOutboxMessage)));
    }

    @Override
    public List<OrderPaymentOutboxMessage> findByTypeAndOutboxStatusAndSagaStatuses(
            String type, OutboxStatus outboxStatus, SagaStatus... sagaStatuses) {
        List<PaymentOutboxEntity> paymentOutboxEntities = paymentOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(
                type, outboxStatus, Arrays.asList(sagaStatuses));

        if(paymentOutboxEntities.isEmpty()) {
            log.debug("Cannot find PaymentOutboxEntity for saga type: {}", type);
        }

        return paymentOutboxEntities.stream()
                .map(paymentOutboxDataMapper::paymentOutboxEntityToOrderPaymentOutboxMessage)
                .toList();
    }

    @Override
    public Optional<OrderPaymentOutboxMessage> findByTypeAndSagaIdAndSagaStatuses(
            String type, UUID sagaId, SagaStatus... sagaStatuses) {
        return paymentOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(
                type, sagaId, Arrays.asList(sagaStatuses))
                .map(paymentOutboxDataMapper::paymentOutboxEntityToOrderPaymentOutboxMessage);
    }

    @Override
    public void deleteByTypeAndOutboxStatusAndSagaStatuses(
            String type, OutboxStatus outboxStatus, SagaStatus... sagaStatuses) {
        paymentOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(
                type, outboxStatus, Arrays.asList(sagaStatuses));
    }
}
