/* Copyright 2022 freecodeformat.com */
package data.lineage.forwarder.bean;
import java.util.Date;

public class AuditEventLog {

    private String specversion;
    private String id;
    private String source;
    private String type;
    private String datacontenttype;
    private String subject;
    private Date time;
    private String route;
    private Data data;
    public void setSpecversion(String specversion) {
         this.specversion = specversion;
     }
     public String getSpecversion() {
         return specversion;
     }

    public void setId(String id) {
         this.id = id;
     }
     public String getId() {
         return id;
     }

    public void setSource(String source) {
         this.source = source;
     }
     public String getSource() {
         return source;
     }

    public void setType(String type) {
         this.type = type;
     }
     public String getType() {
         return type;
     }

    public void setDatacontenttype(String datacontenttype) {
         this.datacontenttype = datacontenttype;
     }
     public String getDatacontenttype() {
         return datacontenttype;
     }

    public void setSubject(String subject) {
         this.subject = subject;
     }
     public String getSubject() {
         return subject;
     }

    public void setTime(Date time) {
         this.time = time;
     }
     public Date getTime() {
         return time;
     }

    public void setRoute(String route) {
         this.route = route;
     }
     public String getRoute() {
         return route;
     }

    public void setData(Data data) {
         this.data = data;
     }
     public Data getData() {
         return data;
     }

}