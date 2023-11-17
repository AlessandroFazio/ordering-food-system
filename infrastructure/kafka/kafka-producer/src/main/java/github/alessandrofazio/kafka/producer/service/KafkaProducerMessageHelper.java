package github.alessandrofazio.kafka.producer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@Component
public class KafkaProducerMessageHelper {
    public <K extends Serializable,V extends SpecificRecordBase> ProducerListener<K, V> getProducerListener(
            String topicName, V avroModel, String orderId) {
        return new ProducerListener<>() {
            @Override
            public void onSuccess(ProducerRecord<K, V> producerRecord, RecordMetadata recordMetadata) {
                log.info("Received successful response from Kafka for order id: {} " +
                                " Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        orderId,
                        recordMetadata.topic(),
                        recordMetadata.partition(),
                        recordMetadata.offset(),
                        recordMetadata.timestamp());
            }

            @Override
            public void onError(ProducerRecord<K, V> producerRecord, RecordMetadata recordMetadata, Exception exception) {
                log.error("Error while sending {} message {} to topic {}",
                        avroModel.getClass().getSimpleName(),
                        avroModel, topicName, exception);
            }
        };
    }
}
