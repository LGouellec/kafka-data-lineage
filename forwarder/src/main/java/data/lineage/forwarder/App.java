package data.lineage.forwarder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.lineage.forwarder.avro.FetchRequest;
import data.lineage.forwarder.avro.ProduceRequest;
import data.lineage.forwarder.bean.AuditEventLog;
import data.lineage.forwarder.mapper.Mapper;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class App 
{
    public static String SOURCE_TOPIC = "confluent-audit-log-events";
    public static String FETCH_REQUEST_TOPIC = "data-lineage-fetch-requests";
    public static String PRODUCE_REQUEST_TOPIC = "data-lineage-produce-requests";
    public static String DLQ_REQUEST_TOPIC = "data-lineage-dlq-requests";
    public static String EXCLUDE_TOPIC = "^_(.*)|^connect(.*)|^data-lineage(.*)";

    public static void main( String[] args )
    {
        final String bootstrapServers = "localhost:19094";
        final Properties streamsConfiguration = new Properties();
        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
        // against which the application is run.
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "data-lineage-forwarder");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "data-lineage-forwarder-client");
        // Where to find Kafka broker(s).
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Specify default (de)serializers for record keys and for record values.
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        streamsConfiguration.put("sasl.mechanism", "SCRAM-SHA-256");
        streamsConfiguration.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"kafkabroker\" password=\"password\";");
        streamsConfiguration.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, "true");
        streamsConfiguration.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        final String prefix = "KAFKA_";
        Map<String, String> systemKafkaProperties = System.getenv().entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(prefix))
                .collect(Collectors.toMap(
                        e -> e.getKey()
                                .replace(prefix, "")
                                .toLowerCase()
                                .replace("_", ".")
                        , e -> e.getValue())
                );

        for(String key : systemKafkaProperties.keySet())
            streamsConfiguration.put(key, systemKafkaProperties.get(key));

        Map<String, Object> configAvroSerde = new HashMap<>();
        for(Map.Entry<Object, Object> kv : streamsConfiguration.entrySet())
            configAvroSerde.put(kv.getKey().toString(), kv.getValue());

        if(System.getenv("FORWARDER_SOURCE_TOPIC") != null)
            SOURCE_TOPIC = System.getenv("FORWARDER_SOURCE_TOPIC");

        if(System.getenv("FORWARDER_FETCH_TOPIC") != null)
            FETCH_REQUEST_TOPIC = System.getenv("FORWARDER_FETCH_TOPIC");

        if(System.getenv("FORWARDER_PRODUCE_TOPIC") != null)
            PRODUCE_REQUEST_TOPIC = System.getenv("FORWARDER_PRODUCE_TOPIC");

        if(System.getenv("FORWARDER_DLQ_TOPIC") != null)
            DLQ_REQUEST_TOPIC = System.getenv("FORWARDER_DLQ_TOPIC");

        if(System.getenv("FORWARDER_EXCLUDE_TOPIC") != null)
            EXCLUDE_TOPIC = System.getenv("FORWARDER_EXCLUDE_TOPIC");

        final StreamsBuilder builder = new StreamsBuilder();
        final ObjectMapper objectMapper = new ObjectMapper();

        SpecificAvroSerde<FetchRequest> fetchSerde = new SpecificAvroSerde<>();
        fetchSerde.configure(configAvroSerde, false);

        SpecificAvroSerde<ProduceRequest> produceSerde = new SpecificAvroSerde<>();
        produceSerde.configure(configAvroSerde, false);

        KStream<Bytes, AuditEventLog> auditEventLogKStream = builder
                .stream(SOURCE_TOPIC, Consumed.with(Serdes.Bytes(), Serdes.String()))
                        .mapValues((k,v) -> {
                            AuditEventLog auditEventLog = null;
                            try {
                                auditEventLog = objectMapper.readValue(v, AuditEventLog.class);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                                return null;
                            }
                            return auditEventLog;
                        });

        Map<String, KStream<Bytes, AuditEventLog>> branchs = auditEventLogKStream
                .filter((k,v) -> v != null)
                .split(Named.as("request-"))
                .branch((k,v) -> v.getData().getAuthorizationInfo().getResourceType().equals("Topic") && v.getData().getMethodName().equals("kafka.FetchConsumer"), Branched.as("fetch"))
                .branch((k,v) -> v.getData().getAuthorizationInfo().getResourceType().equals("Topic") && v.getData().getMethodName().equals("kafka.Produce"), Branched.as("produce"))
                .defaultBranch(Branched.as("dlq"));

        branchs
                .get("request-fetch")
                .filterNot((k,v) -> Pattern.matches(EXCLUDE_TOPIC, Mapper.getTopic(v)))
                .map((k,v) -> new KeyValue<>(Mapper.getTopic(v), Mapper.getFetchRequest(v)))
                .to(FETCH_REQUEST_TOPIC, Produced.with(Serdes.String(), fetchSerde));

        branchs
                .get("request-produce")
                .filterNot((k,v) -> Pattern.matches(EXCLUDE_TOPIC, Mapper.getTopic(v)))
                .map((k,v) -> new KeyValue<>(Mapper.getTopic(v), Mapper.getProduceRequest(v)))
                .to(PRODUCE_REQUEST_TOPIC, Produced.with(Serdes.String(), produceSerde));

        branchs
                .get("request-dlq")
                .map((k,v) -> new KeyValue<>(Mapper.getTopic(v), Mapper.getStringAuditLog(v)))
                .to(DLQ_REQUEST_TOPIC, Produced.with(Serdes.String(), Serdes.String()));

        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
        streams.start();

        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
