package data.lineage.processing.notification;

import data.lineage.processing.avro.DataLineageAggregation;
import data.lineage.processing.helper.KafkaConfigHelper;
import data.lineage.processing.mapper.TopicMapper;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class KafkaConsumerNotification {

    @Value("${NOTIFICATION_TOPIC:data-lineage-notification}")
    public String NOTIFICATION_TOPIC;

    @Autowired
    SimpMessagingTemplate template;

    private static final String KAFKA_ENV_PREFIX = "KAFKA_CONSUMER_";
    private final Properties properties;
    private KafkaConsumer<String, DataLineageAggregation> consumer;
    private boolean isCancel = false;
    private Thread internalThread;

    private Map<String, Object> defaultProps = Map.of(
            ConsumerConfig.GROUP_ID_CONFIG, "data-lineage-forwarder-notification-" + UUID.randomUUID(),
            ConsumerConfig.CLIENT_ID_CONFIG, "data-lineage-forwarder-notification",
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19094",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SpecificAvroDeserializer.class,
            AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT",
            "sasl.mechanism", "SCRAM-SHA-256",
            "sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"kafkabroker\" password=\"password\";",
            KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, "true",
            KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

    public KafkaConsumerNotification() {
        properties = KafkaConfigHelper.buildProperties(defaultProps, System.getenv(), KAFKA_ENV_PREFIX);
    }

    private void run(){
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(List.of(NOTIFICATION_TOPIC));

        while(!isCancel){
            ConsumerRecords<String, DataLineageAggregation> records = consumer.poll(Duration.ofMillis(250));
            if(!records.isEmpty()){
                for(ConsumerRecord<String, DataLineageAggregation> r : records)
                    template.convertAndSend("/topic/message", TopicMapper.getTopicDTO(r.value()));
            }
        }

        consumer.commitSync();
    }

    @PostConstruct
    public void start() {
        Runnable runnable = () -> {
            run();
        };
        internalThread = new Thread(runnable);
        internalThread.start();
    }

    @PreDestroy
    public void close() throws InterruptedException {
        isCancel = true;
        internalThread.join(5000);
        consumer.unsubscribe();
        consumer.close(Duration.ofSeconds(10));
    }
}
