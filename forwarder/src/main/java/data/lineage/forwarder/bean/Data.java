/* Copyright 2022 freecodeformat.com */
package data.lineage.forwarder.bean;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

public class Data {

    @JsonProperty("serviceName")
    private String servicename;
    @JsonProperty("methodName")
    private String methodname;
    @JsonProperty("resourceName")
    private String resourcename;
    @JsonProperty("authenticationInfo")
    private Authenticationinfo authenticationinfo;
    @JsonProperty("authorizationInfo")
    private Authorizationinfo authorizationinfo;
    private Request request;
    @JsonProperty("requestMetadata")
    private Requestmetadata requestmetadata;

    public void setServiceName(String servicename) {
         this.servicename = servicename;
     }
     public String getServiceName() {
         return servicename;
     }

    public void setMethodName(String methodname) {
         this.methodname = methodname;
     }
     public String getMethodName() {
         return methodname;
     }

    public void setResourceName(String resourcename) {
         this.resourcename = resourcename;
     }
     public String getResourceName() {
         return resourcename;
     }

    public void setAuthenticationInfo(Authenticationinfo authenticationinfo) {
         this.authenticationinfo = authenticationinfo;
     }
     public Authenticationinfo getAuthenticationInfo() {
         return authenticationinfo;
     }

    public void setAuthorizationInfo(Authorizationinfo authorizationinfo) {
         this.authorizationinfo = authorizationinfo;
     }
     public Authorizationinfo getAuthorizationInfo() {
         return authorizationinfo;
     }

    public void setRequest(Request request) {
         this.request = request;
     }
     public Request getRequest() {
         return request;
     }

    public void setRequestMetadata(Requestmetadata requestmetadata) {
         this.requestmetadata = requestmetadata;
     }
     public Requestmetadata getRequestmetadata() {
         return requestmetadata;
     }

}