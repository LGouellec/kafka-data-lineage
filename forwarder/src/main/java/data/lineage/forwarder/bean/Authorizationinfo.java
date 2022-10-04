/* Copyright 2022 freecodeformat.com */
package data.lineage.forwarder.bean;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

public class Authorizationinfo {

    private boolean granted;
    private String operation;
    @JsonProperty("resourceType")
    private String resourcetype;
    @JsonProperty("resourceName")
    private String resourcename;
    @JsonProperty("patternType")
    private String patterntype;
    @JsonProperty("aclAuthorization")
    private Aclauthorization aclauthorization;
    public void setGranted(boolean granted) {
         this.granted = granted;
     }
     public boolean getGranted() {
         return granted;
     }

    public void setOperation(String operation) {
         this.operation = operation;
     }
     public String getOperation() {
         return operation;
     }

    public void setResourceType(String resourcetype) {
         this.resourcetype = resourcetype;
     }
     public String getResourceType() {
         return resourcetype;
     }

    public void setResourceName(String resourcename) {
         this.resourcename = resourcename;
     }
     public String getResourceName() {
         return resourcename;
     }

    public void setPatternType(String patterntype) {
         this.patterntype = patterntype;
     }
     public String getPatternType() {
         return patterntype;
     }

    public void setAclAuthorization(Aclauthorization aclauthorization) {
         this.aclauthorization = aclauthorization;
     }
     public Aclauthorization getAclAuthorization() {
         return aclauthorization;
     }

}