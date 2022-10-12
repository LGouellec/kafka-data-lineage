package data.lineage.processing.api;

import io.confluent.ksql.api.client.QueryInfo;

import java.util.List;


public interface KsqlClient {
    List<QueryInfo> getQueries();
}
