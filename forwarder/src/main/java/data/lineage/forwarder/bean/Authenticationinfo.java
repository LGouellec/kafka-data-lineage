/* Copyright 2022 freecodeformat.com */
package data.lineage.forwarder.bean;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

public class Authenticationinfo {

    private String principal;
    @JsonProperty("principalResourceId")
    private String principalresourceid;
    public void setPrincipal(String principal) {
         this.principal = principal;
     }
     public String getPrincipal() {
         return principal;
     }

    public void setPrincipalResourceId(String principalresourceid) {
         this.principalresourceid = principalresourceid;
     }
     public String getPrincipalresourceid() {
         return principalresourceid;
     }

}