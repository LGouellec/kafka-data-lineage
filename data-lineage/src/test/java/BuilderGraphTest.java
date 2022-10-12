import data.lineage.processing.api.impl.ConnectRestClient;
import data.lineage.processing.api.impl.InMemoryConnectClient;
import data.lineage.processing.api.impl.KsqlRestClient;
import data.lineage.processing.dto.TopicDTO;
import data.lineage.processing.graph.BuilderGraph;
import data.lineage.processing.graph.Graph;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BuilderGraphTest {

    // TODO : assert tests

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

    @Test
    public void buildGraphWithKsqlDb() throws ExecutionException, InterruptedException {

        List<TopicDTO> topics = new ArrayList<>();

        TopicDTO topic = new TopicDTO();
        topic.setTopic("stock_trades");
        topic.setProducers(List.of("_confluent-ksql-default_query_CTAS_STOCK_TRADES_WINDOWED_1-507734bf-438e-4e44-bcb8-fddfc980bd16-StreamThread-2-producer"));
        topics.add(topic);

        Graph graph = BuilderGraph
                .withTopics(topics)
                .withKsqlNamingConvention("_confluent-ksql-default_query_(.*)-[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}-StreamThread-[0-9]*-(consumer|producer)", 1)
                .withKsqlClient(new KsqlRestClient("localhost", 8088))
                .withConnectNamingConvention("^connect-(.*)", 1)
                .withConnectClient(new InMemoryConnectClient())
                .buildGraph();
    }

    @Test
    public void buildGraphWithKsqlDbConsumerProducer() throws ExecutionException, InterruptedException {

        List<TopicDTO> topics = new ArrayList<>();

        TopicDTO topic = new TopicDTO();
        topic.setTopic("stock_trades");
        topic.setConsumers(List.of(
                "_confluent-ksql-default_query_CTAS_STOCK_TRADES_WINDOWED_1-507734bf-438e-4e44-bcb8-fddfc980bd16-StreamThread-4-consumer",
                "_confluent-ksql-default_query_CTAS_STOCK_TRADES_WINDOWED_1-507734bf-438e-4e44-bcb8-fddfc980bd16-StreamThread-3-consumer"));
        topic.setProducers(List.of("connect-datagen-trades"));
        topics.add(topic);

        TopicDTO topic2 = new TopicDTO();
        topic2.setTopic("stock_trades_windowed");
        topic2.setConsumers(List.of("connect-elasticsearch-trades"));
        topic2.setProducers(
                List.of(
                        "_confluent-ksql-default_query_CTAS_STOCK_TRADES_WINDOWED_1-507734bf-438e-4e44-bcb8-fddfc980bd16-StreamThread-2-producer",
                        "_confluent-ksql-default_query_CTAS_STOCK_TRADES_WINDOWED_1-507734bf-438e-4e44-bcb8-fddfc980bd16-StreamThread-4-producer"));
        topics.add(topic2);

        Graph graph = BuilderGraph
                .withTopics(topics)
                .withKsqlNamingConvention("_confluent-ksql-default_query_(.*)-[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}-StreamThread-[0-9]*-(consumer|producer)", 1)
                .withKsqlClient(new KsqlRestClient("localhost", 8088))
                .withConnectNamingConvention("^connect-(.*)", 1)
                .withConnectClient(new ConnectRestClient("http://localhost:8083"))
                .buildGraph();
    }

    @Test
    public void buildGraphWithKsqlDbOtherOrderConsumerProducer() throws ExecutionException, InterruptedException {

        List<TopicDTO> topics = new ArrayList<>();

        TopicDTO topic = new TopicDTO();
        topic.setTopic("stock_trades");
        topic.setConsumers(List.of("_confluent-ksql-default_query_CTAS_STOCK_TRADES_WINDOWED_1-507734bf-438e-4e44-bcb8-fddfc980bd16-StreamThread-4-consumer"));
        topic.setProducers(List.of("connect-datagen-trades"));
        TopicDTO topic2 = new TopicDTO();
        topic2.setTopic("stock_trades_windowed");
        topic2.setConsumers(List.of("connect-elasticsearch-trades"));
        topic2.setProducers(List.of("_confluent-ksql-default_query_CTAS_STOCK_TRADES_WINDOWED_1-507734bf-438e-4e44-bcb8-fddfc980bd16-StreamThread-2-producer"));

        topics.add(topic2);
        topics.add(topic);

        Graph graph = BuilderGraph
                .withTopics(topics)
                .withKsqlNamingConvention("_confluent-ksql-default_query_(.*)-[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}-StreamThread-[0-9]*-(consumer|producer)", 1)
                .withKsqlClient(new KsqlRestClient("localhost", 8088))
                .withConnectNamingConvention("^connect-(.*)", 1)
                .withConnectClient(new ConnectRestClient("http://localhost:8083"))
                .buildGraph();
    }

    @Test
    public void buildGraphWithKsqlDbOtherOrderConsumerProducerMixing() throws ExecutionException, InterruptedException {

        List<TopicDTO> topics = new ArrayList<>();

        TopicDTO topic = new TopicDTO();
        topic.setTopic("stock_trades");
        topic.setConsumers(List.of("_confluent-ksql-default_query_CTAS_STOCK_TRADES_WINDOWED_1-507734bf-438e-4e44-bcb8-fddfc980bd16-StreamThread-4-consumer", "consumer1"));
        topic.setProducers(List.of("connect-datagen-trades"));
        TopicDTO topic2 = new TopicDTO();
        topic2.setTopic("stock_trades_windowed");
        topic2.setConsumers(List.of("connect-elasticsearch-trades"));
        topic2.setProducers(List.of("_confluent-ksql-default_query_CTAS_STOCK_TRADES_WINDOWED_1-507734bf-438e-4e44-bcb8-fddfc980bd16-StreamThread-2-producer"));

        topics.add(topic2);
        topics.add(topic);

        Graph graph = BuilderGraph
                .withTopics(topics)
                .withKsqlNamingConvention("_confluent-ksql-default_query_(.*)-[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}-StreamThread-[0-9]*-(consumer|producer)", 1)
                .withKsqlClient(new KsqlRestClient("localhost", 8088))
                .withConnectNamingConvention("^connect-(.*)", 1)
                .withConnectClient(new ConnectRestClient("http://localhost:8083"))
                .buildGraph();
    }

    @Test
    public void build3Graphs() throws ExecutionException, InterruptedException {

        List<TopicDTO> topics = new ArrayList<>();

        TopicDTO topic = new TopicDTO();
        topic.setTopic("stock_trades");
        topic.setConsumers(List.of("client2"));
        topic.setProducers(List.of("client1"));
        TopicDTO topic2 = new TopicDTO();
        topic2.setTopic("stock_trades_windowed");
        topic2.setConsumers(List.of("client3"));
        topic2.setProducers(List.of("client2"));
        TopicDTO topic3 = new TopicDTO();
        topic3.setTopic("stock_trades_windowed_final");
        topic3.setConsumers(List.of("client4"));
        topic3.setProducers(List.of("client3"));

        topics.add(topic2);
        topics.add(topic);
        topics.add(topic3);

        Graph graph = BuilderGraph
                .withTopics(topics)
                .buildGraph();
    }
}
