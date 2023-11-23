package github.alessandrofazio.payment.service.messaging.publisher.kafka;

import github.alessandrofazio.kafka.order.avro.model.PaymentResponseAvroModel;
import github.alessandrofazio.kafka.producer.service.KafkaProducer;
import github.alessandrofazio.kafka.producer.service.KafkaProducerMessageHelper;
import github.alessandrofazio.outbox.OutboxStatus;
import github.alessandrofazio.payment.service.domain.config.PaymentServiceConfigData;
import github.alessandrofazio.payment.service.domain.outbox.model.OrderEventPayload;
import github.alessandrofazio.payment.service.domain.outbox.model.OrderOutboxMessage;
import github.alessandrofazio.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import github.alessandrofazio.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.BiConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventKafkaPublisher implements PaymentResponseMessagePublisher {

    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaProducer<UUID, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducerMessageHelper kafkaProducerMessageHelper;


    @Override
    public void publish(
            OrderOutboxMessage orderOutboxMessage, BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {

        OrderEventPayload orderEventPayload = kafkaProducerMessageHelper.getEventPayload(
                        orderOutboxMessage.getPayload(), OrderEventPayload.class);

        UUID sagaId = orderOutboxMessage.getSagaId();

        log.info("Received OrderOutboxMessage for order id: {} and saga id: {}",
                orderEventPayload.getOrderId(), sagaId.toString());

        String topicName = paymentServiceConfigData.getPaymentResponseTopicName();
        PaymentResponseAvroModel paymentResponseAvroModel =
                paymentMessagingDataMapper.orderEventPayloadToPaymentResponseAvroModel(orderEventPayload, sagaId);

        try {
            kafkaProducer.send(
                    topicName,
                    sagaId,
                    paymentResponseAvroModel,
                    kafkaProducerMessageHelper.getProducerListener(
                            topicName,
                            paymentResponseAvroModel,
                            orderOutboxMessage,
                            outboxCallback,
                            orderEventPayload.getOrderId()));

            log.info("PaymentResponseAvroModel sent to kafka for order id: {} and saga id: {}",
                    paymentResponseAvroModel.getOrderId(), sagaId.toString());
        } catch (Exception e) {
            log.error("Error while sending PaymentResponseAvroModel message " +
                    " to kafka with order id: {} and saga id: {}, error: {}",
                    orderEventPayload.getOrderId(), sagaId.toString(), e.getMessage());
        }
    }
}
