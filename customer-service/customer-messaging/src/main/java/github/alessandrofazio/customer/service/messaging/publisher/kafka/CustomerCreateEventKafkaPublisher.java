package github.alessandrofazio.customer.service.messaging.publisher.kafka;

import github.alessandrofazio.customer.service.domain.config.CustomerServiceConfigData;
import github.alessandrofazio.customer.service.domain.event.CustomerCreatedEvent;
import github.alessandrofazio.customer.service.domain.ports.output.message.publisher.CustomerMessagePublisher;
import github.alessandrofazio.customer.service.messaging.mapper.CustomerMessagingDataMapper;
import github.alessandrofazio.kafka.order.avro.model.CustomerAvroModel;
import github.alessandrofazio.kafka.producer.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerCreateEventKafkaPublisher implements CustomerMessagePublisher {

    private final CustomerMessagingDataMapper customerMessagingDataMapper;
    private final KafkaProducer<UUID, CustomerAvroModel> kafkaProducer;
    private final CustomerServiceConfigData customerServiceConfigData;

    @Override
    public void publish(CustomerCreatedEvent customerCreatedEvent) {
        log.info("Received CustomerCreatedEvent for customer with id: {}",
                customerCreatedEvent.getCustomerId().getValue());
        String topicName = customerServiceConfigData.getCustomerTopicName();
        CustomerAvroModel messageValue = customerMessagingDataMapper.customerCreatedEventToCustomerAvroModel(customerCreatedEvent);

        try {
            kafkaProducer.send(
                    topicName,
                    customerCreatedEvent.getCustomerId().getValue(),
                    messageValue,
                    getProducerListener(messageValue, topicName)
            );
        } catch (Exception e) {
            log.error("Error while sending CustomerCreatedEvent to Kafka for customer with id: {}, error: {}",
                    customerCreatedEvent.getCustomerId().getValue(), e.getMessage());
        }
    }

    private ProducerListener<UUID, CustomerAvroModel> getProducerListener(CustomerAvroModel message, String topicName) {
        return new ProducerListener<>() {
            @Override
            public void onSuccess(ProducerRecord<UUID, CustomerAvroModel> producerRecord, RecordMetadata recordMetadata) {
                log.info("Received record metadata -> Topic: {}, Partition: {}, Offset: {}, Timestamp: {}, at time {}",
                        recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(),
                        recordMetadata.timestamp(), System.nanoTime());
            }

            @Override
            public void onError(ProducerRecord<UUID, CustomerAvroModel> producerRecord, RecordMetadata recordMetadata, Exception exception) {
                log.error("Error while sending message {} to topic {}", message, topicName);
            }
        };
    }
}
