package data.lineage.processing.dto;

import java.util.Collections;
import java.util.List;

public class TopicDTO {
    private String topic;
    private List<String> consumers;
    private List<String> producers;

    public TopicDTO() {
        consumers = Collections.emptyList();
        producers = Collections.emptyList();
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<String> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<String> consumers) {
        this.consumers = consumers;
    }

    public List<String> getProducers() {
        return producers;
    }

    public void setProducers(List<String> producers) {
        this.producers = producers;
    }
}
