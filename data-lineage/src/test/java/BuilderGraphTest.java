import data.lineage.processing.api.impl.InMemoryConnectClient;
import data.lineage.processing.dto.TopicDTO;
import data.lineage.processing.graph.BuilderGraph;
import data.lineage.processing.graph.Graph;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuilderGraphTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void buildGraphSingleTopic()
    {
        List<TopicDTO> topics = new ArrayList<>();
        TopicDTO topic = new TopicDTO();
        topic.setTopic("stock_trades");
        topic.setConsumers(Collections.singleton("consumer1").stream().toList());
        topic.setProducers(Collections.singleton("producer1").stream().toList());
        topics.add(topic);

        Graph graph = BuilderGraph.withTopics(topics).buildGraph();
    }

    @Test
    public void buildGraphSingleTopicMultipleConsumersAndProducers()
    {
        List<TopicDTO> topics = new ArrayList<>();
        TopicDTO topic = new TopicDTO();
        topic.setTopic("stock_trades");
        topic.setConsumers(List.of("consumer1", "consumer2", "consumer3"));
        topic.setProducers(List.of("producer1", "producer2", "producer3"));
        topics.add(topic);

        Graph graph = BuilderGraph.withTopics(topics).buildGraph();
    }

    @Test
    public void buildGraphMulitpleTopics()
    {
        List<TopicDTO> topics = new ArrayList<>();

        TopicDTO topic = new TopicDTO();
        topic.setTopic("stock_trades");
        topic.setConsumers(List.of("consumer1"));
        topic.setProducers(List.of("producer1", "producer2"));
        topics.add(topic);

        TopicDTO topic2 = new TopicDTO();
        topic2.setTopic("agg");
        topic2.setProducers(List.of("producer3"));
        topics.add(topic2);

        Graph graph = BuilderGraph.withTopics(topics).buildGraph();
    }


    @Test
    public void buildGraphWithConnector()
    {
        List<TopicDTO> topics = new ArrayList<>();

        TopicDTO topic = new TopicDTO();
        topic.setTopic("stock_trades");
        topic.setConsumers(List.of("consumer1"));
        topic.setProducers(List.of("connect-mysql-source"));
        topics.add(topic);

        Graph graph = BuilderGraph
                .withTopics(topics)
                .withConnectNamingConvention("^connect-(.*)", 1)
                .withConnectClient(new InMemoryConnectClient())
                .buildGraph();
    }
}
