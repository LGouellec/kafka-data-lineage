/* Copyright 2022 freecodeformat.com */
package data.lineage.forwarder.bean;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

public class Request {

    @JsonProperty("correlation_id")
    private String correlationId;
    @JsonProperty("client_id")
    private String clientId;

    public void setCorrelation_id(String correlationId) {
         this.correlationId = correlationId;
     }
     public String getCorrelation_id() {
         return correlationId;
     }

    public void setClient_id(String clientId) {
         this.clientId = clientId;
     }
     public String getClient_id() {
         return clientId;
     }

}