package data.lineage.processing.controller;

import data.lineage.processing.avro.DataLineageAggregation;
import data.lineage.processing.dto.TopicDTO;
import data.lineage.processing.mapper.Mapper;
import data.lineage.processing.streams.TopologyKafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "/topics", method = RequestMethod.GET, produces = "application/json")
    public List<TopicDTO> getDayProduct() {

        List<TopicDTO> topics = new ArrayList<>();

        ReadOnlyKeyValueStore<String, DataLineageAggregation> store = streams
                .getStreams()
                .store(StoreQueryParameters.fromNameAndType(TopologyKafkaStreams.AGGRATION_STORE, QueryableStoreTypes.keyValueStore()));

        KeyValueIterator<String, DataLineageAggregation> iterator =  store.all();
        while(iterator.hasNext())
            topics.add(Mapper.getTopicDTO(iterator.next().value));
        iterator.close();

        return topics;
    }
}
