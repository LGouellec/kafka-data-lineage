/* Copyright 2022 freecodeformat.com */
package data.lineage.forwarder.bean;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

public class Aclauthorization {

    @JsonProperty("permissionType")
    private String permissiontype;
    private String host;

    public void setPermissionType(String permissiontype) {
         this.permissiontype = permissiontype;
     }
     public String getPermissionType() {
         return permissiontype;
     }

    public void setHost(String host) {
         this.host = host;
     }
     public String getHost() {
         return host;
     }

}