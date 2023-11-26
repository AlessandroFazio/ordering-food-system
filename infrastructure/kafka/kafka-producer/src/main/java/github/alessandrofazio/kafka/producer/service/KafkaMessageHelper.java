package github.alessandrofazio.kafka.producer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.alessandrofazio.order.service.domain.exception.OrderDomainException;
import github.alessandrofazio.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.function.BiConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageHelper {

    private final ObjectMapper objectMapper;

    public <K extends Serializable, V extends SpecificRecordBase, U> ProducerListener<K, V> getProducerListener(
            String topicName, V avroModel, U outboxMessage, BiConsumer<U, OutboxStatus> outboxCallback, String orderId) {
        return new ProducerListener<>() {
            @Override
            public void onSuccess(
                    ProducerRecord<K, V> producerRecord, RecordMetadata recordMetadata) {
                log.info("Received successful response from Kafka for order id: {} " +
                                " Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        orderId,
                        recordMetadata.topic(),
                        recordMetadata.partition(),
                        recordMetadata.offset(),
                        recordMetadata.timestamp());

                outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED);
            }

            @Override
            public void onError(
                    ProducerRecord<K, V> producerRecord, RecordMetadata recordMetadata, Exception exception) {
                log.error("Error while sending {} message {} and outbox type {} to topic {}",
                        avroModel.getClass().getSimpleName(),
                        avroModel, outboxMessage.getClass().getName(), topicName, exception);
                outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
            }
        };
    }

    public <R> R getEventPayload(String payload, Class<R> clazz) {
        try {
            return clazz.cast(objectMapper.readValue(payload, clazz));
        } catch (JsonProcessingException e) {
            log.error("Could not read " + clazz.getSimpleName() + " object", e);
            throw new OrderDomainException(
                    "Could not read " + clazz.getSimpleName() + " object", e);
        }
    }
}
