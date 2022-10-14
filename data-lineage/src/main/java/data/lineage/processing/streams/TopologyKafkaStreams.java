package data.lineage.processing.streams;

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Component
public class TopologyKafkaStreams {

    @Value("${FETCH_REQUEST_TOPIC:data-lineage-fetch-requests}")
    public String FETCH_REQUEST_TOPIC;

    @Value("${PRODUCE_REQUEST_TOPIC:data-lineage-produce-requests}")
    public String PRODUCE_REQUEST_TOPIC;

    @Value("${AGGREGATION_STORE:aggregation-store}")
    public String AGGREGATION_STORE;

    private static final String KAFKA_ENV_PREFIX = "KAFKA_";
    private final Properties properties;
    private final TopologyDefinition definition;
    private final Logger logger = LoggerFactory.getLogger(TopologyKafkaStreams.class);
    private KafkaStreams streams;

    public TopologyKafkaStreams() {
        properties = buildProperties(defaultProps, System.getenv(), KAFKA_ENV_PREFIX);
        definition = new TopologyDefinition();
    }

    public KafkaStreams getStreams() {
        return streams;
    }

    private Map<String, Object> defaultProps = Map.of(
            StreamsConfig.APPLICATION_ID_CONFIG, "data-lineage-processing",
        StreamsConfig.CLIENT_ID_CONFIG, "data-lineage-processing-client",
        StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19094",
        StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName(),
        StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName(),
        AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT",
        "sasl.mechanism", "SCRAM-SHA-256",
        "sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"kafkabroker\" password=\"password\";",
        KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, "true",
        KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

    private Properties buildProperties(Map<String, Object> baseProps, Map<String, String> envProps, String prefix) {
        Map<String, String> systemProperties = envProps.entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(prefix))
                .collect(Collectors.toMap(
                        e -> e.getKey()
                                .replace(prefix, "")
                                .toLowerCase()
                                .replace("_", ".")
                        , e -> e.getValue())
                );

        Properties props = new Properties();
        props.putAll(baseProps);
        props.putAll(systemProperties);
        return props;
    }

    @PostConstruct
    public void start(){

        Map<String, Object> config = new HashMap<>();
        for(Map.Entry<Object, Object> kv : properties.entrySet())
            config.put(kv.getKey().toString(), kv.getValue());

        logger.info("Creating streams with props: {}", properties);
        Topology topology = definition.buildTopology(
                new StreamsBuilder(),
                config,
                FETCH_REQUEST_TOPIC,
                PRODUCE_REQUEST_TOPIC,
                AGGREGATION_STORE);

        logger.info(topology.describe().toString());

        streams = new KafkaStreams(topology,properties);
        streams.start();
    }

    @PreDestroy
    public void close(){
        if(streams != null)
            streams.close(Duration.ofSeconds(30));
    }
}
