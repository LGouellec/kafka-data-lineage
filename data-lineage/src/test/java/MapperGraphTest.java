import data.lineage.processing.dto.GraphDTO;
import data.lineage.processing.dto.TopicDTO;
import data.lineage.processing.graph.BuilderGraph;
import data.lineage.processing.graph.Graph;
import data.lineage.processing.mapper.GraphMapper;
import data.lineage.processing.mapper.TopicMapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MapperGraphTest {

    @Test
    public void mapGraph() {

        List<TopicDTO> topics = new ArrayList<>();

        TopicDTO topic = new TopicDTO();
        topic.setTopic("stock_trades");
        topic.setConsumers(List.of("client2", "client191991"));
        topic.setProducers(List.of("client1"));
        TopicDTO topic2 = new TopicDTO();
        topic2.setTopic("stock_trades_windowed");
        topic2.setConsumers(List.of("client3"));
        topic2.setProducers(List.of("client2"));
        TopicDTO topic3 = new TopicDTO();
        topic3.setTopic("stock_trades_windowed_final");
        topic3.setConsumers(List.of("client4"));
        topic3.setProducers(List.of("client3"));
        TopicDTO topic4 = new TopicDTO();
        topic4.setTopic("test");
        topic4.setConsumers(List.of("client10"));
        topic4.setProducers(List.of("client12"));

        topics.add(topic2);
        topics.add(topic);
        topics.add(topic3);
        topics.add(topic4);

        Graph graph = BuilderGraph
                .withTopics(topics)
                .buildGraph();

        GraphDTO dto = (new GraphMapper()).getGraphDTO(graph);
    }

}
