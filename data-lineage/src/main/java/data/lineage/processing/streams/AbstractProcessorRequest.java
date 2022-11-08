package data.lineage.processing.streams;


import data.lineage.forwarder.avro.FetchRequest;
import data.lineage.forwarder.avro.ProduceRequest;
import data.lineage.processing.avro.DataLineageAggregation;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.ArrayList;
import java.util.Collections;

public class AbstractProcessorRequest<V>  implements Transformer<String, V, KeyValue<String, DataLineageAggregation>> {

    private KeyValueStore<String, DataLineageAggregation> store;
    private boolean fetchRequest;
    private String storeName;
    private ProcessorContext context;

    public AbstractProcessorRequest(boolean fetchRequest, String storeName){
        this.fetchRequest = fetchRequest;
        this.storeName = storeName;
    }

    private DataLineageAggregation createAgg(boolean consumer, String key, String clientId) {
        DataLineageAggregation newAgg = new DataLineageAggregation();
        newAgg.setTopic(key);
        if(consumer) {
            newAgg.setConsumers(Collections.singletonList(clientId));
            newAgg.setProducers(new ArrayList<>());
        }else{
            newAgg.setProducers(Collections.singletonList(clientId));
            newAgg.setConsumers(new ArrayList<>());
        }
        return newAgg;
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        store = context.getStateStore(storeName);
    }

    @Override
    public KeyValue<String, DataLineageAggregation> transform(String readOnlyKey, V value) {
        if(fetchRequest) {
            FetchRequest fetch = (FetchRequest) value;
            DataLineageAggregation oldAgg = store.get(readOnlyKey);
            if(oldAgg != null){
                if(fetch.getLeaveGroup()){
                    ArrayList<String> consumers = new ArrayList<>(oldAgg.getConsumers());
                    consumers.remove(fetch.getClientId());
                    oldAgg.setConsumers(consumers);
                    store.put(readOnlyKey, oldAgg);
                    return new KeyValue<>(readOnlyKey, oldAgg);
                }
                else {
                    if (!oldAgg.getConsumers().contains(fetch.getClientId())) {
                        oldAgg.getConsumers().add(fetch.getClientId());
                        store.put(readOnlyKey, oldAgg);
                        return new KeyValue<>(readOnlyKey, oldAgg);
                    }
                }
            }
            else if(!fetch.getLeaveGroup()){
                DataLineageAggregation newAgg = createAgg(true, readOnlyKey, fetch.getClientId());
                store.put(readOnlyKey, newAgg);
                return new KeyValue<>(readOnlyKey, newAgg);
            }
        }
        else{
            ProduceRequest produce = (ProduceRequest) value;
            DataLineageAggregation oldAgg = store.get(readOnlyKey);
            if(oldAgg!= null){
                if(!oldAgg.getProducers().contains(produce.getClientId())){
                    oldAgg.getProducers().add(produce.getClientId());
                    store.put(readOnlyKey, oldAgg);
                    return new KeyValue<>(readOnlyKey, oldAgg);
                }
            }
            else{
                DataLineageAggregation newAgg = createAgg(false, readOnlyKey, produce.getClientId());
                store.put(readOnlyKey, newAgg);
                return new KeyValue<>(readOnlyKey, newAgg);
            }
        }

        return null;
    }

    @Override
    public void close() {

    }
}
