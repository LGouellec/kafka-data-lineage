#!/bin/bash

export TAG=7.2.1

yellow=`tput setaf 3`
reset=`tput sgr0`
POSITIONAL_ARGS=()
RUN=true

while [[ $# -gt 0 ]]; do
  case $1 in
    --run)
      RUN=true
      shift # past argument
      ;;
    -*|--*)
      echo "Unknown option $1"
      exit 1
      ;;
    *)
      POSITIONAL_ARGS+=("$1") # save positional arg
      shift # past argument
      ;;
  esac
done

set -- "${POSITIONAL_ARGS[@]}" # restore positional parameters
echo "${yellow}RUN DATA LINEAGE PRODUCTS = ${RUN}${reset}"

docker-compose -f stack/docker-compose.yml build
docker-compose -f stack/docker-compose.yml down -v --remove-orphans
docker-compose -f stack/docker-compose.yml up -d zookeeper-add-kafka-users zookeeper
sleep 10
docker-compose -f stack/docker-compose.yml up -d zookeeper broker

# Create other users
docker exec kafka-broker-1 kafka-configs --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --alter --add-config 'SCRAM-SHA-256=[password=julie-secret],SCRAM-SHA-512=[password=julie-secret]' --entity-type users --entity-name julie
docker exec kafka-broker-1 kafka-configs --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --alter --add-config 'SCRAM-SHA-256=[password=allan-secret],SCRAM-SHA-512=[password=allan-secret]' --entity-type users --entity-name allan

echo "Add ACLs for schema-registry"
docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:schemaregistry' --allow-host '*' \
               --producer --consumer --topic _schemas --group schema-registry

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:schemaregistry' --allow-host '*' \
               --operation DescribeConfigs --topic _schemas

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:schemaregistry' --allow-host '*' \
               --operation Describe --topic _schemas

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:schemaregistry' --allow-host '*' \
               --operation Read --topic _schemas

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:schemaregistry' --allow-host '*' \
               --operation Write --topic _schemas

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:schemaregistry' --allow-host '*' \
               --operation Describe --topic __consumer_offsets

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:schemaregistry' --allow-host '*' \
               --operation Create --cluster kafka-cluster

echo "Add ACLs for connect"
docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:connect' --allow-host '*' \
               --producer --consumer --topic connect-configs --group connect-cluster

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:connect' --allow-host '*' \
               --operation Describe --topic connect-configs

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:connect' --allow-host '*' \
               --producer --consumer --topic connect-offsets --group connect-cluster

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:connect' --allow-host '*' \
               --operation Describe --topic connect-offsets

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:connect' --allow-host '*' \
               --producer --consumer --topic connect-status --group connect-cluster

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:connect' --allow-host '*' \
               --operation Describe --topic connect-status

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:connect' --allow-host '*' \
               --operation Read --group connect-cluster

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:connect' --allow-host '*' \
               --operation Create --cluster kafka-cluster

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
               --allow-principal 'User:connect' --allow-host '*' \
               --producer --idempotent --topic '*'

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                --allow-principal 'User:connect' --allow-host '*' \
                --operation Describe --operation Write --topic _confluent-monitoring

echo "Add ACLs for ksqldb"
docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:ksqldb' --allow-host '*' \
                                    --operation DescribeConfigs --cluster

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:ksqldb' --allow-host '*' \
                                    --operation Read --topic stock_trades

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:ksqldb' --allow-host '*' \
                                    --operation All --resource-pattern-type prefixed --topic _confluent-ksql --group _confluent-ksql

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:ksqldb' --allow-host '*' \
                                    --operation Write --operation Describe --topic '*' --transactional-id '*'

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:ksqldb' --allow-host '*' \
                                    --operation Create --topic '*'

# TODO : ksqldb output topic

echo "Add ACLs for control-center"
docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:client' --allow-host '*' \
                                    --operation All --operation Describe --operation DescribeConfigs --cluster kafka-cluster

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:client' --allow-host '*' \
                                    --operation Read --operation Describe --operation DescribeConfigs --topic '*'

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:client' --allow-host '*' \
                                    --operation Read --operation Describe --group '*'

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:client' --allow-host '*' \
                                    --operation All --operation DescribeConfigs --topic _confluent --resource-pattern-type prefixed

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:client' --allow-host '*' \
                                    --operation Describe --operation Read --topic _confluent --resource-pattern-type prefixed

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:client' --allow-host '*' \
                                    --operation All --topic _confluent-metrics

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:client' --allow-host '*' \
                                    --operation All --topic _confluent-monitoring

docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:client' --allow-host '*' \
                                    --group ConfluentTelemetryReporter --resource-pattern-type prefixed --operation Describe
                    
# Start others
docker-compose -f stack/docker-compose.yml up -d schema-registry connect control-center ksqldb-server ksqldb-cli elasticsearch

./scripts/wait-for-connect-and-controlcenter.sh $@

# Create topic
echo "Create stock_trades topic"
docker exec kafka-broker-1 kafka-topics --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --create --topic stock_trades --partitions 4 --replication-factor 1

echo "Create data lineage topics"
docker exec kafka-broker-1 kafka-topics --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --create --topic data-lineage-fetch-requests --partitions 8 --replication-factor 1
docker exec kafka-broker-1 kafka-topics --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --create --topic data-lineage-produce-requests --partitions 8 --replication-factor 1
docker exec kafka-broker-1 kafka-topics --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --create --topic data-lineage-dlq-requests --partitions 8 --replication-factor 1
docker exec kafka-broker-1 kafka-topics --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --create --topic data-lineage-notification --partitions 3 --replication-factor 1

echo "Add ACLs for julie"
docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:julie' --allow-host '*' \
                                    --producer --idempotent --topic stock_trades

echo "Add ACLs for allan"
docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:allan' --allow-host '*' \
                                    --consumer --topic stock_trades_windowed --group connect-elasticsearch-trades

echo "Create datagen source connector"
curl -s -X PUT \
      -H "Content-Type: application/json" \
      --data '{
                "connector.class": "io.confluent.kafka.connect.datagen.DatagenConnector",
                "kafka.topic": "stock_trades",
                "quickstart": "stock_trades",
                "tasks.max": "1",
                "producer.override.client.id": "connect-datagen-trades",
                "producer.override.sasl.jaas.config": "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"julie\" password=\"julie-secret\";"
            }' \
      http://localhost:8083/connectors/datagen-trades/config | jq


# Start data lineage services
if [ "$RUN" = "true" ]; then
    echo "🚀Start data lineage products"
    docker-compose -f stack/docker-compose.yml  up -d data-lineage-forwarder data-lineage-api data-lineage-ui
fi

echo "🚀 Create the ksqlDB stream"
docker exec -i ksqldb-cli bash -c 'echo -e "\n\n⏳ Waiting for ksqlDB to be available before launching CLI\n"; while [[ $(curl -s -o /dev/null -w %{http_code} http://ksqldb-server:8088/) -eq 000 ]] ; do echo -e $(date) "KSQL Server HTTP state: " $(curl -s -o /dev/null -w %{http_code} http:/ksqldb-server:8088/) " (waiting for 200)" ; sleep 10 ; done; ksql http://ksqldb-server:8088' << EOF

SET 'auto.offset.reset' = 'earliest';

CREATE OR REPLACE STREAM STOCK_TRADES (
   quantity BIGINT,
   price BIGINT,
   userid VARCHAR,
   side VARCHAR)
   WITH (KAFKA_TOPIC='stock_trades', VALUE_FORMAT='AVRO');

CREATE OR REPLACE TABLE STOCK_TRADES_WINDOWED
    WITH (kafka_topic='stock_trades_windowed') AS
    SELECT USERID,
           AVG(QUANTITY * PRICE) AS AVG_STOCK_TOTAL,
           WINDOWEND as WINDOW_END,
           AS_VALUE(USERID) USER
    FROM STOCK_TRADES
    WINDOW TUMBLING (SIZE 5 MINUTES)
    WHERE SIDE = 'BUY'
    GROUP BY USERID;

EOF

sleep 3;
echo "Create elastic sink connector" 
curl -X PUT \
           -H "Content-Type: application/json" \
           --data '{
                "connector.class": "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
                "connection.url": "http://elasticsearch:9200",
                "type.name": "_doc",
                "behavior.on.malformed.documents": "warn",
                "errors.tolerance": "all",
                "errors.log.enable": "true",
                "errors.log.include.messages": "true",
                "topics": "stock_trades_windowed",
                "key.ignore": "true",
                "schema.ignore": "true",
                "consumer.override.client.id": "connect-elasticsearch-trades",
                "consumer.override.sasl.jaas.config": "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"allan\" password=\"allan-secret\";"
            }' \
           http://localhost:8083/connectors/elasticsearch-trades/config | jq .

if [ "$RUN" = "true" ]; then
    echo "🚀 All the stack is running, feel free to go on http://localhost:80. Enjoy your visualization ! 🎉 "
fi