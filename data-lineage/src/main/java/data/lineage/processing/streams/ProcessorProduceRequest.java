package data.lineage.processing.streams;

import data.lineage.forwarder.avro.ProduceRequest;

public class ProcessorProduceRequest extends AbstractProcessorRequest<ProduceRequest> {
    public ProcessorProduceRequest(String storeName) {
        super(false, storeName);
    }
}
