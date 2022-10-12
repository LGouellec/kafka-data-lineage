package data.lineage.processing.graph;

import data.lineage.processing.api.ConnectClient;
import data.lineage.processing.api.ConnectDef;
import data.lineage.processing.api.KsqlClient;
import data.lineage.processing.dto.TopicDTO;
import io.confluent.ksql.api.client.QueryInfo;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuilderGraph {

    private List<TopicDTO> topics;
    private ConnectClient connectClient;
    private KsqlClient ksqlClient;
    private String connectNamingConvention;
    private int connectGroupConnectorNameIndex;
    private String streamNamingConvention;
    private String ksqlNamingConvention;
    private int ksqlGroupQueryNameIndex;

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

    public BuilderGraph withConnectNamingConvention(String connectNamingConvention, int connectGroupConnectorNameIndex) {
        this.connectNamingConvention = connectNamingConvention;
        this.connectGroupConnectorNameIndex = connectGroupConnectorNameIndex;
        return this;
    }

    public BuilderGraph withStreamNamingConvention(String streamNamingConvention) {
        this.streamNamingConvention = streamNamingConvention;
        return this;
    }

    public BuilderGraph withKsqlNamingConvention(String ksqlNamingConvention, int ksqlGroupQueryNameIndex) {
        this.ksqlNamingConvention = ksqlNamingConvention;
        this.ksqlGroupQueryNameIndex = ksqlGroupQueryNameIndex;
        return this;
    }

    public static BuilderGraph withTopics(List<TopicDTO> topics){
        return new BuilderGraph(topics);
    }

    public Graph buildGraph() {

        boolean mapConnect = false, mapKsql = false;
        Map<String, ConnectDef> connectorDefinitions = new HashMap<>();
        List<QueryInfo> queriesKsql = new ArrayList<>();
        Pattern connectRegex = null, ksqlDbRegex = null;
        Graph graph = Graph.buildGraph(Node.buildNode(Node.ROOT, Node.ROOT));

        if(connectClient != null){
            mapConnect = true;
            connectRegex = Pattern.compile(connectNamingConvention);
            connectorDefinitions = connectClient.getConnectorDefinitions();
        }

        if(ksqlClient != null){
            mapKsql = true;
            ksqlDbRegex = Pattern.compile(ksqlNamingConvention);
            queriesKsql = ksqlClient.getQueries();
        }

        for(TopicDTO t : topics)
        {
            List<Node> producersNodes = new ArrayList<>();
            for(String p : t.getProducers()){
                boolean alreadyAdd = false;
                Node producerNode = null;
                if(mapConnect && connectRegex.matcher(p).find()){
                    Matcher matcher = connectRegex.matcher(p);
                    matcher.find();
                    String connectName = matcher.group(connectGroupConnectorNameIndex);

                    if(connectorDefinitions.containsKey(connectName))
                        producerNode = Node.buildNode(connectorDefinitions.get(connectName).getConnectorName(), "connector");
                    else
                    {
                        Node existingNode = graph.getNode(p);
                        if(existingNode != null){
                            producerNode = existingNode;
                            alreadyAdd = true;
                        }
                        else
                            producerNode = Node.buildNode(p, "producer");
                    }
                }
                else if(mapKsql && ksqlDbRegex.matcher(p).find()){
                    Matcher matcher = ksqlDbRegex.matcher(p);
                    matcher.find();
                    String queryName = matcher.group(ksqlGroupQueryNameIndex);

                    Optional<QueryInfo> queryInfo = queriesKsql.stream().filter(q -> q.getId().equals(queryName)).findFirst();
                    if(queryInfo.isPresent()){
                        Node existingNode = graph.getNode(queryInfo.get().getId());
                        if(existingNode != null){
                            producerNode = existingNode;
                            alreadyAdd = true;
                        }
                        else
                            producerNode = Node.buildNode(queryInfo.get().getId(), "ksql-query");
                    }
                    else
                    {
                        Node existingNode = graph.getNode(p);
                        if(existingNode != null){
                            producerNode = existingNode;
                            alreadyAdd = true;
                        }
                        else
                            producerNode = Node.buildNode(p, "producer");
                    }
                }
                else {
                    Node existingNode = graph.getNode(p);
                    if(existingNode != null){
                        producerNode = existingNode;
                        alreadyAdd = true;
                    }
                    else
                        producerNode = Node.buildNode(p, "producer");
                }

                producersNodes.add(producerNode);
                if(!alreadyAdd)
                    graph.addGraphNode(graph.getFirstNode(), producerNode);
            }

            Node topicNode = Node.buildNode(t.getTopic(), "topic");

            for(Node pNode : producersNodes)
                graph.addGraphNode(pNode, topicNode);

            for(String c : t.getConsumers()){
                Node consumerNode = null;
                if(mapConnect && connectRegex.matcher(c).find()){
                    Matcher matcher = connectRegex.matcher(c);
                    matcher.find();
                    String connectName = matcher.group(connectGroupConnectorNameIndex);

                    if(connectorDefinitions.containsKey(connectName))
                        consumerNode = Node.buildNode(connectorDefinitions.get(connectName).getConnectorName(), "connector");
                    else {
                        Node existingNode = graph.getNode(c);
                        if(existingNode != null){
                            consumerNode = existingNode;
                        }
                        else
                            consumerNode = Node.buildNode(c, "consumer");
                    }
                }
                else if(mapKsql && ksqlDbRegex.matcher(c).find()){
                    Matcher matcher = ksqlDbRegex.matcher(c);
                    matcher.find();
                    String queryName = matcher.group(ksqlGroupQueryNameIndex);

                    Optional<QueryInfo> queryInfo = queriesKsql.stream().filter(q -> q.getId().equals(queryName)).findFirst();
                    if(queryInfo.isPresent()){
                        Node existingNode = graph.getNode(queryInfo.get().getId());
                        if(existingNode != null){
                            consumerNode = existingNode;
                        }
                        else
                            consumerNode = Node.buildNode(queryInfo.get().getId(), "ksql-query");
                    }
                    else {
                        Node existingNode = graph.getNode(c);
                        if(existingNode != null){
                            consumerNode = existingNode;
                        }
                        else
                            consumerNode = Node.buildNode(c, "consumer");
                    }
                }
                else {
                    Node existingNode = graph.getNode(c);
                    if(existingNode != null){
                        consumerNode = existingNode;
                    }
                    else
                        consumerNode = Node.buildNode(c, "consumer");
                }
                graph.addGraphNode(topicNode, consumerNode);
            }
        }

        return graph;
    }
}
