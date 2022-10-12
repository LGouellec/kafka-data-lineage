package data.lineage.processing.streams;

import data.lineage.forwarder.avro.FetchRequest;
import data.lineage.forwarder.avro.ProduceRequest;
import data.lineage.processing.avro.DataLineageAggregation;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.Stores;

import java.util.Map;

public class TopologyDefinition {

    public Topology buildTopology(
            StreamsBuilder builder,
            Map<String, Object> config,
            String fetchTopic,
            String produceTopic,
            String aggStore){

        SpecificAvroSerde<FetchRequest> fetchSerde = new SpecificAvroSerde<>();
        fetchSerde.configure(config, false);
        SpecificAvroSerde<ProduceRequest> produceSerde = new SpecificAvroSerde<>();
        produceSerde.configure(config, false);
        SpecificAvroSerde<DataLineageAggregation> aggSerde = new SpecificAvroSerde<>();
        aggSerde.configure(config, false);

       builder.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore(aggStore),
                        Serdes.String(),
                        aggSerde));

        KStream<String, FetchRequest> fetchStream = builder.stream(fetchTopic, Consumed.with(Serdes.String(), fetchSerde));
        KStream<String, ProduceRequest> produceStream = builder.stream(produceTopic, Consumed.with(Serdes.String(), produceSerde));

        KStream<String, DataLineageAggregation> fetchEnriched = fetchStream
                .transform(() -> new ProcessorFetchRequest(aggStore), aggStore);

        KStream<String, DataLineageAggregation> producedEnriched =  produceStream
                .transform(() -> new ProcessorProduceRequest(aggStore), aggStore);

        fetchEnriched.merge(producedEnriched)
                .to("data-lineage-notification", Produced.with(Serdes.String(), aggSerde));

        Topology topology = builder.build();

        return topology;
    }
}
