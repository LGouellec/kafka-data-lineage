/* Copyright 2022 freecodeformat.com */
package data.lineage.forwarder.bean;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

public class Requestmetadata {

    @JsonProperty("client_address")
    private String clientAddress;

    public void setClient_address(String clientAddress) {
         this.clientAddress = clientAddress;
     }
     public String getClient_address() {
         return clientAddress;
     }

}