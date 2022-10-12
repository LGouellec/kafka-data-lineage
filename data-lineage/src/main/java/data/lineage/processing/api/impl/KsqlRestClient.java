package data.lineage.processing.api.impl;

import data.lineage.processing.api.KsqlClient;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.QueryInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class KsqlRestClient implements KsqlClient {

    private final String ksqlHost;
    private final int ksqlPort;

    public KsqlRestClient(String ksqlHost, int ksqlPort){

        this.ksqlHost = ksqlHost;
        this.ksqlPort = ksqlPort;
    }

    @Override
    public List<QueryInfo> getQueries() {
        ClientOptions options = ClientOptions.create()
                .setHost(ksqlHost)
                .setPort(ksqlPort);
        Client client = Client.create(options);
        List<QueryInfo> queries = new ArrayList<>();
        try {
            queries = client.listQueries().get();
            client.close();
        }catch(InterruptedException | ExecutionException e){
            // nothing
        }finally {
            client.close();
        }
        return queries;
    }
}
