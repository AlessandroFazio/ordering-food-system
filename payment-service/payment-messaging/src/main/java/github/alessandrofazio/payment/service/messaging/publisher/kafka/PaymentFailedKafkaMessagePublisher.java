package github.alessandrofazio.payment.service.messaging.publisher.kafka;
import github.alessandrofazio.kafka.order.avro.model.PaymentResponseAvroModel;
import github.alessandrofazio.kafka.producer.service.KafkaProducer;
import github.alessandrofazio.kafka.producer.service.KafkaProducerMessageHelper;
import github.alessandrofazio.payment.service.domain.config.PaymentServiceConfigData;
import github.alessandrofazio.payment.service.domain.event.PaymentCancelledEvent;
import github.alessandrofazio.payment.service.domain.event.PaymentCompletedEvent;
import github.alessandrofazio.payment.service.domain.event.PaymentFailedEvent;
import github.alessandrofazio.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import github.alessandrofazio.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import github.alessandrofazio.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import github.alessandrofazio.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFailedKafkaMessagePublisher implements PaymentFailedMessagePublisher {

    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<UUID, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaProducerMessageHelper kafkaProducerMessageHelper;

    @Override
    public void publish(PaymentFailedEvent domainEvent) {
        UUID orderId = domainEvent.getPayment().getOrderId().getValue();

        log.info("Received " + domainEvent.getClass().getSimpleName() +
                " for order with id: {}", orderId);

        try {
            kafkaProducer.send(
                    paymentServiceConfigData.getPaymentRequestTopicName(),
                    orderId,
                    paymentMessagingDataMapper.paymentFailedEventTopaymentResponseAvroModel(domainEvent),
                    kafkaProducerMessageHelper.getProducerListener(
                            paymentServiceConfigData.getPaymentRequestTopicName(),
                            paymentMessagingDataMapper.paymentFailedEventTopaymentResponseAvroModel(domainEvent),
                            orderId.toString()));

            log.info("PaymentResponseAvroModel sent to kafka for order with id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending PaymentResponseAvroModel message " +
                    " to kafka for order with id: {}, error: {}", orderId, e.getMessage());
        }

    }
}

