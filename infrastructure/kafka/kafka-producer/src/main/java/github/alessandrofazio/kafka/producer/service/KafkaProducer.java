package github.alessandrofazio.kafka.producer.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.SendResult;

import java.io.Serializable;
import java.util.function.BiConsumer;

public interface KafkaProducer<K extends Serializable, V extends SpecificRecordBase> {
    void send(String topicName, K key, V message, ProducerListener<K,V> producerListener);
}
