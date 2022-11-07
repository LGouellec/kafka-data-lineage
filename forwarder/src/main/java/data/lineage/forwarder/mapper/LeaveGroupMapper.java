package data.lineage.forwarder.mapper;

import data.lineage.forwarder.avro.FetchRequest;
import data.lineage.forwarder.bean.AuditEventLog;
import data.lineage.forwarder.mapper.Mapper;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.DescribeConsumerGroupsResult;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class LeaveGroupMapper implements KeyValueMapper<Bytes, AuditEventLog, Iterable<KeyValue<String, FetchRequest>>> {

    private final Properties streamsConfiguration;
    private AdminClient adminClient = null;

    public LeaveGroupMapper(Properties streamsConfiguration) {
        this.streamsConfiguration = streamsConfiguration;
    }

    @Override
    public Iterable<KeyValue<String, FetchRequest>> apply(Bytes key, AuditEventLog value) {
        if(adminClient == null)
            createAdminClient();

        try {
            String groupId = value.getData().getAuthorizationInfo().getResourceName();
            Map<String, Map<TopicPartition, OffsetAndMetadata>> results = adminClient
                    .listConsumerGroupOffsets(groupId)
                    .all()
                    .get();

            if(results.containsKey(groupId)){
                List<String> topics = results.get(groupId).keySet().stream().map(tp -> tp.topic()).distinct().collect(Collectors.toList());
                return topics.stream().map(t -> {
                    FetchRequest request = Mapper.getFetchRequest(value);
                    request.setTopic(t);
                    request.setLeaveGroup(true);
                    return new KeyValue<>(t, request);
                }).collect(Collectors.toList());
            }
            else
                return Collections.emptyList();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private void createAdminClient() {
        adminClient = AdminClient.create(streamsConfiguration);
    }
}
