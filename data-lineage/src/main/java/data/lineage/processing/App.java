package data.lineage.processing;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import data.lineage.processing.avro.FetchRequest;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class App {

    public static String FETCH_REQUEST_TOPIC = "data-lineage-fetch-requests";
    public static String PRODUCE_REQUEST_TOPIC = "data-lineage-produce-requests";

    public static void main(String[] args) {

        final String bootstrapServers = "localhost:19094";
        final Properties streamsConfiguration = new Properties();
        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
        // against which the application is run.
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "data-lineage-processing");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "data-lineage-processing-client");
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

        for(String key : System.getenv().keySet())
            if(key.startsWith("KAFKA_"))
                streamsConfiguration.put(key.replace("KAFKA_", ""), System.getenv(key));

        Map<String, Object> configAvroSerde = new HashMap<>();
        for(Map.Entry<Object, Object> kv : streamsConfiguration.entrySet())
            configAvroSerde.put(kv.getKey().toString(), kv.getValue());

        if(System.getenv("FORWARDER_FETCH_TOPIC") != null)
            FETCH_REQUEST_TOPIC = System.getenv("FORWARDER_FETCH_TOPIC");

        if(System.getenv("FORWARDER_PRODUCE_TOPIC") != null)
            PRODUCE_REQUEST_TOPIC = System.getenv("FORWARDER_PRODUCE_TOPIC");

        final StreamsBuilder builder = new StreamsBuilder();
        final ObjectMapper objectMapper = new ObjectMapper();

        SpecificAvroSerde<FetchRequest> fetchSerde = new SpecificAvroSerde<>();
        fetchSerde.configure(configAvroSerde, false);

        SpecificAvroSerde<ProduceRequest> produceSerde = new SpecificAvroSerde<>();
        produceSerde.configure(configAvroSerde, false);

    }
}
