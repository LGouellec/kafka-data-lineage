package data.lineage.processing.mapper;

import data.lineage.processing.avro.DataLineageAggregation;
import data.lineage.processing.dto.TopicDTO;

public class TopicMapper {

    public static TopicDTO getTopicDTO(DataLineageAggregation dataLineageAggregation){
        TopicDTO dto = new TopicDTO();
        dto.setTopic(dataLineageAggregation.getTopic());
        dto.setConsumers(dataLineageAggregation.getConsumers());
        dto.setProducers(dataLineageAggregation.getProducers());
        return dto;
    }

}
