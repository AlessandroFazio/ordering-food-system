package github.alessandrofazio.order.service.messaging.listerner.kafka;

import debezium.payment.order_outbox.Envelope;
import debezium.payment.order_outbox.Value;
import github.alessandrofazio.kafka.consumer.KafkaConsumer;
import github.alessandrofazio.kafka.order.avro.model.PaymentStatus;
import github.alessandrofazio.kafka.producer.service.KafkaMessageHelper;
import github.alessandrofazio.messaging.debezium.DebeziumOp;
import github.alessandrofazio.order.service.domain.exception.OrderNotFoundException;
import github.alessandrofazio.order.service.messaging.mapper.OrderMessagingDataMapper;
import github.alessandrofazio.domain.event.payload.PaymentOrderEventPayload;
import github.alessandrofazio.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResponseKafkaListener implements KafkaConsumer<Envelope> {

    private final PaymentResponseMessageListener paymentResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${order-service.payment-response-topic-name}")
    public void receive(@Payload List<Envelope> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<UUID> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of payment responses received with keys:{}, partitions:{} and offsets: {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        Predicate<Envelope> isCreateOp = message -> message.getBefore() == null &&
                message.getOp().equals(DebeziumOp.CREATE.getValue());

        log.info("{} number of payment responses received",
                messages.stream().filter(isCreateOp).toList().size());

        messages.stream()
                .filter(isCreateOp)
                .forEach(avroModel -> {
                    log.info("Incoming message in PaymentResponseKafkaListener: {}", avroModel);
                    Value paymentResponseAvroModel = avroModel.getAfter();
                    PaymentOrderEventPayload paymentOrderEventPayload = kafkaMessageHelper.getEventPayload(
                            paymentResponseAvroModel.getPayload(), PaymentOrderEventPayload.class);
                try {
                    if (PaymentStatus.COMPLETED.name().equals(paymentOrderEventPayload.getPaymentStatus())) {
                        log.info("Processing successful payment for order id: {}", paymentOrderEventPayload.getOrderId());
                        paymentResponseMessageListener.paymentCompleted(
                                orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(
                                        paymentOrderEventPayload, paymentResponseAvroModel));

                    } else if (PaymentStatus.CANCELLED.name().equals(paymentOrderEventPayload.getPaymentStatus()) ||
                            PaymentStatus.FAILED.name().equals(paymentOrderEventPayload.getPaymentStatus())) {
                        log.info("Processing unsuccessful payment for order id: {}", paymentOrderEventPayload.getOrderId());
                        paymentResponseMessageListener.paymentCancelled(
                                orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(
                                        paymentOrderEventPayload, paymentResponseAvroModel));
                    }
                } catch (OptimisticLockingFailureException e) {
                    // NO-OP for optimistic lock.
                    // This means another thread finished the work, do not throw error to prevent reading the data from kafka again
                    log.error("Caught optimistic locking exception in PaymentResponseKafkaListener for order with id: {}",
                            paymentOrderEventPayload.getOrderId());
                } catch (OrderNotFoundException e) {
                    log.error("No order found for order id: {}", paymentOrderEventPayload.getOrderId());
                } catch (DataAccessException e) {
                    SQLException sqlException = (SQLException) e.getRootCause();
                    if(sqlException != null && sqlException.getSQLState() != null &&
                            PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                        // NO-OP for unique constraint exception
                        log.error("Caught unique constraint exception with sql state: {} " +
                                        " in PaymentResponseKafkaListener for order id: {}",
                                sqlException.getSQLState(), paymentOrderEventPayload.getOrderId());
                    }
                }});
    }
}
