package data.lineage.processing.controller;

import data.lineage.processing.api.impl.ConnectRestClient;
import data.lineage.processing.api.impl.KsqlRestClient;
import data.lineage.processing.avro.DataLineageAggregation;
import data.lineage.processing.dto.GraphDTO;
import data.lineage.processing.dto.TopicDTO;
import data.lineage.processing.graph.BuilderGraph;
import data.lineage.processing.graph.Graph;
import data.lineage.processing.mapper.GraphMapper;
import data.lineage.processing.mapper.TopicMapper;
import data.lineage.processing.streams.TopologyKafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("lineage")
public class DataLineageController {

    @Autowired
    private TopologyKafkaStreams streams;

    @Value("${KSQLDB_NAMING_CONVENTION:_confluent-ksql-default_query_(.*)-[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}-StreamThread-[0-9]*-(consumer|producer)}")
    public String KSQLDB_NAMING_CONVENTION;

    @Value("${KSQLDB_GROUP_NAMEQUERY_INDEX:1}")
    public int KSQLDB_GROUP_NAMEQUERY_INDEX;

    @Value("${CONNECT_NAMING_CONVENTION:^connect-(.*)}")
    public String CONNECT_NAMING_CONVENTION;

    @Value("${CONNECT_GROUP_CONNECTOR_NAME_INDEX:1}")
    public int CONNECT_GROUP_CONNECTOR_NAME_INDEX;

    @Value("${KAFKA_CONNECT_HOST:localhost}")
    public String KAFKA_CONNECT_HOST;

    @Value("${KAFKA_CONNECT_PORT:8083}")
    public int KAFKA_CONNECT_PORT;

    @Value("${KSQLDB_HOST:localhost}")
    public String KSQLDB_HOST;

    @Value("${KSQLDB_PORT:8088}")
    public int KSQLDB_PORT;

    @RequestMapping(value = "/topics", method = RequestMethod.GET, produces = "application/json")
    public List<TopicDTO> getTopics() {

        List<TopicDTO> topics = new ArrayList<>();

        ReadOnlyKeyValueStore<String, DataLineageAggregation> store = streams
                .getStreams()
                .store(StoreQueryParameters.fromNameAndType(streams.AGGREGATION_STORE, QueryableStoreTypes.keyValueStore()));

        KeyValueIterator<String, DataLineageAggregation> iterator =  store.all();
        while(iterator.hasNext())
            topics.add(TopicMapper.getTopicDTO(iterator.next().value));
        iterator.close();

        return topics;
    }

    @RequestMapping(value = "/graph", method = RequestMethod.GET, produces = "application/json")
    public GraphDTO getGraph(){

        List<TopicDTO> topics = getTopics();

        Graph graph = BuilderGraph
                .withTopics(topics)
                .withKsqlNamingConvention(KSQLDB_NAMING_CONVENTION, KSQLDB_GROUP_NAMEQUERY_INDEX)
                .withKsqlClient(new KsqlRestClient(KSQLDB_HOST, KSQLDB_PORT))
                .withConnectNamingConvention(CONNECT_NAMING_CONVENTION, CONNECT_GROUP_CONNECTOR_NAME_INDEX)
                .withConnectClient(new ConnectRestClient("http://"+KAFKA_CONNECT_HOST+":" + KAFKA_CONNECT_PORT))
                .buildGraph();

        return (new GraphMapper()).getGraphDTO(graph);
    }
}
