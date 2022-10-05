package data.lineage.processing.streams;


import data.lineage.forwarder.avro.FetchRequest;

public class ProcessorFetchRequest extends AbstractProcessorRequest<FetchRequest> {

    public ProcessorFetchRequest(String storeName) {
        super(true, storeName);
    }
}
