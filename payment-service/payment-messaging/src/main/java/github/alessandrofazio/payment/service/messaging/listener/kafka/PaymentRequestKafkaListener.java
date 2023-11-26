package github.alessandrofazio.payment.service.messaging.listener.kafka;

import debezium.order.payment_outbox.Envelope;
import debezium.order.payment_outbox.Value;
import github.alessandrofazio.domain.event.payload.OrderPaymentEventPayload;
import github.alessandrofazio.kafka.consumer.KafkaSingleItemConsumer;
import github.alessandrofazio.kafka.order.avro.model.PaymentOrderStatus;
import github.alessandrofazio.kafka.producer.service.KafkaMessageHelper;
import github.alessandrofazio.messaging.debezium.DebeziumOp;
import github.alessandrofazio.payment.service.domain.exception.PaymentApplicationServiceException;
import github.alessandrofazio.payment.service.domain.exception.PaymentDomainException;
import github.alessandrofazio.payment.service.domain.exception.PaymentNotFoundException;
import github.alessandrofazio.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import github.alessandrofazio.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestKafkaListener implements KafkaSingleItemConsumer<Envelope> {

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}",
    topics = "${payment-service.payment-request-topic-name}")
    public void receive(@Payload Envelope message,
                        @Header(KafkaHeaders.RECEIVED_KEY) UUID key,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                        @Header(KafkaHeaders.OFFSET) Long offset) {
        if(message.getBefore() != null || !DebeziumOp.CREATE.getValue().equals(message.getOp())) return;

        log.info("Incoming messaging PaymentRequestKafkaListener: {} with key: {}, partitions: {} and offset: {}",
                message, key, partition, offset.toString());

        Value paymentRequestAvroModel = message.getAfter();
        OrderPaymentEventPayload orderPaymentEventPayload = kafkaMessageHelper.getEventPayload(
                paymentRequestAvroModel.getPayload(), OrderPaymentEventPayload.class);

        try {
            if (PaymentOrderStatus.PENDING.name().equals(orderPaymentEventPayload.getPaymentOrderStatus())) {
                log.info("Processing payment request for order id: {}", orderPaymentEventPayload.getOrderId());
                paymentRequestMessageListener.completePayment(paymentMessagingDataMapper
                        .orderPaymentEventPayloadTopaymentRequest(
                                orderPaymentEventPayload, paymentRequestAvroModel));
            } else if (PaymentOrderStatus.CANCELLED.name().equals(orderPaymentEventPayload.getPaymentOrderStatus())) {
                log.info("Cancelled payment for order id: {}", orderPaymentEventPayload.getOrderId());
                paymentRequestMessageListener.cancelPayment(paymentMessagingDataMapper
                        .orderPaymentEventPayloadTopaymentRequest(
                                orderPaymentEventPayload, paymentRequestAvroModel));
            }
        } catch (DataAccessException e) {
            SQLException sqlException = (SQLException) e.getRootCause();
            if(sqlException != null && sqlException.getSQLState() != null &&
                    PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                // NO-OP for unique constraint exception
                log.error("Caught unique constraint exception with sql state: {} " +
                        " in PaymentRequestKafkaListener for order id: {}",
                        sqlException.getSQLState(), orderPaymentEventPayload.getOrderId());
            } else {
                throw new PaymentApplicationServiceException("Throwing DataAccessException in " +
                        " PaymentRequestKafkaListener: " + e.getMessage(), e);
            }
            log.error("");
            throw new PaymentDomainException("");
        } catch (PaymentNotFoundException e) {
            log.error("No payment found for order with id: {}", orderPaymentEventPayload.getOrderId());
        }
    }
}
