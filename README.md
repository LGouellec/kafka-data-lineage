# kafka-data-lineage
On-prem Data Lineage based on Confluent Audit Logs

- start elastic container
- Add connector elastic + add ACLS consumer Allan
{
  "name": "ElasticsearchSinkConnectorConnector_0",
  "config": {
    "name": "ElasticsearchSinkConnectorConnector_0",
    "connector.class": "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
    "topics": "stock_trades",
    "connection.url": "http://elasticsearch:9200"
    "producer.override.sasl.jaas.config": "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"allan\" password=\"allan-secret\";"
  }
}

TODO
- Create Graph Algo (continue with ksqldb query parsing + kafka streams naming)
[X] Connector naming
- Change producer.override.client.id for connetor example
- Create ksqldb query + app kafka streams for example
- Create DTO for front ui
- React flow UI + elkjs maybe