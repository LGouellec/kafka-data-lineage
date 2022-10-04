#!/bin/bash

export TAG=7.2.1

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
                                    --operation Read --operation Describe --group _confluent --resource-pattern-type prefixed

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
docker-compose -f stack/docker-compose.yml up -d --build schema-registry connect control-center kcat ksqldb-server ksqldb-cli

./scripts/wait-for-connect-and-controlcenter.sh $@

# Create topic
echo "Create stock_trades topic"
docker exec kafka-broker-1 kafka-topics --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --create --topic stock_trades --partitions 4 --replication-factor 1

echo "Add ACLs for julie"
docker exec kafka-broker-1 kafka-acls --bootstrap-server kafka-broker-1:19094 --command-config /etc/kafka/client-properties/admin.properties --add \
                                    --allow-principal 'User:julie' --allow-host '*' \
                                    --producer --idempotent --topic stock_trades

echo "Create datagen source connector"
curl -s -X PUT \
      -H "Content-Type: application/json" \
      --data '{
                "connector.class": "io.confluent.kafka.connect.datagen.DatagenConnector",
                "kafka.topic": "stock_trades",
                "quickstart": "stock_trades",
                "tasks.max": "1",
                "producer.override.sasl.jaas.config": "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"julie\" password=\"julie-secret\";"
            }' \
      http://localhost:8083/connectors/datagen-trades/config | jq

# # Start kcat both consumers
