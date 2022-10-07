package data.lineage.processing.api.impl;

import data.lineage.processing.api.ConnectClient;
import data.lineage.processing.api.ConnectDef;
import org.sourcelab.kafka.connect.apiclient.Configuration;
import org.sourcelab.kafka.connect.apiclient.KafkaConnectClient;
import org.sourcelab.kafka.connect.apiclient.request.dto.ConnectorDefinition;
import org.sourcelab.kafka.connect.apiclient.request.dto.ConnectorsWithExpandedInfo;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class ConnectRestClient implements ConnectClient {

    private final KafkaConnectClient innerConnectClient;

    public ConnectRestClient(String connectHost){
        final Configuration configuration = new Configuration(connectHost);
        innerConnectClient = new KafkaConnectClient(configuration);
    }

    @Override
    public Map<String, ConnectDef> getConnectorDefinitions() {
        ConnectorsWithExpandedInfo info = innerConnectClient.getConnectorsWithExpandedInfo();
        Collection<ConnectorDefinition> definitions = info.getAllDefinitions();
        return definitions.stream().collect(Collectors.toMap(ConnectorDefinition::getName, (c) -> new ConnectDefImpl(c)));
    }
}
