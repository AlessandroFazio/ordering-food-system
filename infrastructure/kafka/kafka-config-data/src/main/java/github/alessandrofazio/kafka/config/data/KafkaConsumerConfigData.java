package github.alessandrofazio.kafka.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-consumer-config")
public class KafkaConsumerConfigData {
    private String keyDeserializer;
    private String valueDeserializer;
    private String autoOffsetReset;
    private String specificAvroReaderKey;
    private String specificAvroReader;
    private Boolean batchListener;
    private Boolean autoStartup;
    private Integer concurrencyLevel;
    private Integer sessionTimoutMs;
    private Integer heartbeatIntervalMs;
    private Integer maxPollIntervalMs;
    private Long pollTimoutMs;
    private Integer maxPollRecords;
    private Integer maxPartitionFetchBytesDefault;
    private Integer maxPartitionFetchBytesBoostFactor;
}
