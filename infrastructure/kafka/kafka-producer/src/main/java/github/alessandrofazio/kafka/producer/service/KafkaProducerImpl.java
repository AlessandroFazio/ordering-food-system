package github.alessandrofazio.kafka.producer.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K,V> {

    private final KafkaTemplate<K,V> kafkaTemplate;
    @Override
    public void send(String topicName, K key, V message, ProducerListener<K,V> producerListener) {
        log.info("Sending ,message={} to topic={}", message, topicName);
        kafkaTemplate.setProducerListener(producerListener);
        try {
            kafkaTemplate.send(topicName, key, message);
        } catch (KafkaException e) {
            log.error("Error on kafka producer with key: {}, message: {} and exception: {}", key, message, e.getMessage());
            throw new KafkaProducerException(new ProducerRecord<>(topicName, key, message),
                    "Error on kafka producer with key: " + key + " and message: " + message, e);
        }
    }

    @PreDestroy
    public void close() {
        if(kafkaTemplate != null) {
            log.info("Closing kafka producer");
            kafkaTemplate.destroy();
        }
    }
}
