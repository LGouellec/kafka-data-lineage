package data.lineage.processing.api.impl;

import data.lineage.processing.api.ConnectClient;
import data.lineage.processing.api.ConnectDef;
import org.sourcelab.kafka.connect.apiclient.request.dto.ConnectorDefinition;

import java.util.HashMap;
import java.util.Map;

public class InMemoryConnectClient implements ConnectClient {

    public static class MockConnectDef implements ConnectDef {

        private String name;

        public MockConnectDef(String name){

            this.name = name;
        }

        @Override
        public String getConnectorName() {
            return name;
        }
    }

    private Map<String, ConnectDef> connectorDefinitionMap = new HashMap<>();

    public InMemoryConnectClient(){
        connectorDefinitionMap.put("mysql-source", new MockConnectDef("mysql-source"));
        connectorDefinitionMap.put("mysql-sink", new MockConnectDef("mysql-sink"));
    }

    @Override
    public Map<String, ConnectDef> getConnectorDefinitions() {
        return connectorDefinitionMap;
    }
}
