package data.lineage.forwarder.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.lineage.forwarder.avro.FetchRequest;
import data.lineage.forwarder.avro.ProduceRequest;
import data.lineage.forwarder.bean.AuditEventLog;

public class Mapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String getTopic(AuditEventLog event){
        return event.getData().getAuthorizationInfo().getResourceName();
    }

    public static FetchRequest getFetchRequest(AuditEventLog event){
        FetchRequest request = new FetchRequest();
        request.setId(event.getId());
        request.setClientId(event.getData().getRequest().getClient_id());
        request.setMethodName(event.getData().getMethodName());
        request.setOperation(event.getData().getAuthorizationInfo().getOperation());
        request.setTopic(getTopic(event));
        request.setUser(event.getData().getAuthenticationInfo().getPrincipal());
        return request;
    }

    public static ProduceRequest getProduceRequest(AuditEventLog event){
        ProduceRequest request = new ProduceRequest();
        request.setId(event.getId());
        request.setClientId(event.getData().getRequest().getClient_id());
        request.setMethodName(event.getData().getMethodName());
        request.setOperation(event.getData().getAuthorizationInfo().getOperation());
        request.setTopic(getTopic(event));
        request.setUser(event.getData().getAuthenticationInfo().getPrincipal());
        return request;
    }

    public static String getStringAuditLog(AuditEventLog v) {
        try {
            return mapper.writeValueAsString(v);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
