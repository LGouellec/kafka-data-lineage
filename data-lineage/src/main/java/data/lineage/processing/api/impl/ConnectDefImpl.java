package data.lineage.processing.api.impl;

import data.lineage.processing.api.ConnectDef;
import org.sourcelab.kafka.connect.apiclient.request.dto.ConnectorDefinition;

public class ConnectDefImpl implements ConnectDef {

    private ConnectorDefinition def;

    public ConnectDefImpl(ConnectorDefinition def){
        this.def = def;
    }

    @Override
    public String getConnectorName() {
        return def.getName();
    }
}
