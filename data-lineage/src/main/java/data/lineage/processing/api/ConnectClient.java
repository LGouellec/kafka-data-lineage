package data.lineage.processing.api;

import org.sourcelab.kafka.connect.apiclient.request.dto.ConnectorDefinition;

import java.util.Map;

public interface ConnectClient {
    Map<String, ConnectDef> getConnectorDefinitions();
}
