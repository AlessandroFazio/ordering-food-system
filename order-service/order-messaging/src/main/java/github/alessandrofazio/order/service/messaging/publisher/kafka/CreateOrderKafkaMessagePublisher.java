package github.alessandrofazio.order.service.messaging.publisher.kafka;

import github.alessandrofazio.kafka.order.avro.model.PaymentRequestAvroModel;
import github.alessandrofazio.kafka.producer.service.KafkaProducer;
import github.alessandrofazio.kafka.producer.service.KafkaProducerMessageHelper;
import github.alessandrofazio.order.service.domain.event.OrderCreatedEvent;
import github.alessandrofazio.service.domain.config.OrderServiceConfigData;
import github.alessandrofazio.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import github.alessandrofazio.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderKafkaMessagePublisher implements OrderCreatedPaymentRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<UUID, PaymentRequestAvroModel> kafkaProducer;
    private final KafkaProducerMessageHelper kafkaProducerMessageHelper;

    @Override
    public void publish(OrderCreatedEvent domainEvent) {
        log.info("Received " + domainEvent.getClass().getSimpleName() + " for order id: {}",
                domainEvent.getOrder().getId().getValue());

        PaymentRequestAvroModel paymentRequestAvroModel = orderMessagingDataMapper
                .orderCreatedEventToPaymentRequestAvroModel(domainEvent);

        try {
            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                    domainEvent.getOrder().getId().getValue(),
                    paymentRequestAvroModel,
                    kafkaProducerMessageHelper.getProducerListener(
                            orderServiceConfigData.getPaymentRequestTopicName(), paymentRequestAvroModel,
                            domainEvent.getOrder().getId().toString()));

            log.info("PaymentRequestAvroModel sent to Kafka for order id: {}", paymentRequestAvroModel.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending " + PaymentRequestAvroModel.class.getSimpleName() + " message" +
                    " to kafka with order id: {}, error: {}", domainEvent.getOrder().getId().getValue(), e.getMessage());
        }
    }
}
