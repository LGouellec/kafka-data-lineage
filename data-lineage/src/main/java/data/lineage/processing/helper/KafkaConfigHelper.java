package data.lineage.processing.helper;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class KafkaConfigHelper {

    public static Properties buildProperties(Map<String, Object> baseProps, Map<String, String> envProps, String prefix){
        Map<String, String> systemProperties = envProps.entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(prefix))
                .collect(Collectors.toMap(
                        e -> e.getKey()
                                .replace(prefix, "")
                                .toLowerCase()
                                .replace("_", ".")
                        , e -> e.getValue())
                );

        Properties props = new Properties();
        props.putAll(baseProps);
        props.putAll(systemProperties);
        return props;
    }
}
