package github.alessandrofazio.kafka.consumer;

import org.apache.avro.specific.SpecificRecordBase;

import java.util.List;
import java.util.UUID;

public interface KafkaSingleItemConsumer<T extends SpecificRecordBase> {
    void receive(T message, UUID key, Integer partition, Long offset);
}
