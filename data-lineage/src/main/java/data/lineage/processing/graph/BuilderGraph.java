package data.lineage.processing.graph;

import data.lineage.processing.api.ConnectClient;
import data.lineage.processing.api.ConnectDef;
import data.lineage.processing.api.KsqlClient;
import data.lineage.processing.dto.TopicDTO;
import org.sourcelab.kafka.connect.apiclient.request.dto.ConnectorDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuilderGraph {

    private List<TopicDTO> topics;
    private ConnectClient connectClient;
    private KsqlClient ksqlClient;
    private String connectNamingConvention;
    private int connectGroupConnetorNameIndex;
    private String streamNamingConvention;
    private String ksqlNamingConvention;

    private BuilderGraph(List<TopicDTO> topics){
        this.topics = topics;
    }

    public BuilderGraph withConnectClient(ConnectClient connectClient) {
        this.connectClient = connectClient;
        return this;
    }

    public BuilderGraph withKsqlClient(KsqlClient ksqlClient) {
        // https://docs.ksqldb.io/en/latest/developer-guide/ksqldb-clients/java-client/
        this.ksqlClient = ksqlClient;
        return this;
    }

    public BuilderGraph withConnectNamingConvention(String connectNamingConvention, int connectGroupConnetorNameIndex) {
        this.connectNamingConvention = connectNamingConvention;
        this.connectGroupConnetorNameIndex = connectGroupConnetorNameIndex;
        return this;
    }

    public BuilderGraph withStreamNamingConvention(String streamNamingConvention) {
        this.streamNamingConvention = streamNamingConvention;
        return this;
    }

    public BuilderGraph withKsqlNamingConvention(String ksqlNamingConvention) {
        this.ksqlNamingConvention = ksqlNamingConvention;
        return this;
    }

    public static BuilderGraph withTopics(List<TopicDTO> topics){
        return new BuilderGraph(topics);
    }

    public Graph buildGraph() {
        boolean mapConnect = false;
        Map<String, ConnectDef> connectorDefinitions = new HashMap<>();
        Pattern connectRegex = null, ksqlDbRegex = null;
        Graph graph = Graph.buildGraph(Node.buildNode("root", "ROOT"));

        if(connectClient != null){
            mapConnect = true;
            connectRegex = Pattern.compile(connectNamingConvention);
            connectorDefinitions = connectClient.getConnectorDefinitions();
        }

        for(TopicDTO t : topics)
        {
            Node topicNode = Node.buildNode(t.getTopic(), "topic");
            for(String p : t.getProducers()){
                Node producerNode = null;
                if(mapConnect && connectRegex.matcher(p).find()){
                    Matcher matcher = connectRegex.matcher(p);
                    matcher.find();
                    String connectName = matcher.group(connectGroupConnetorNameIndex);

                    if(connectorDefinitions.containsKey(connectName))
                        producerNode = Node.buildNode(connectorDefinitions.get(connectName).getConnectorName(), "connector");
                    else
                        producerNode = Node.buildNode(p, "producer");
                }
                else
                    producerNode = Node.buildNode(p, "producer");

                producerNode.addNextNode(topicNode);
                graph.getFirstNode().addNextNode(producerNode);
            }

            for(String c : t.getConsumers()){
                Node consumerNode = Node.buildNode(c, "consumer");
                topicNode.addNextNode(consumerNode);
            }
        }

        return graph;
    }
}
