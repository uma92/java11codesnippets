import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class YourClass {
    public Map<String, LinkedHashMap> getMessages(String functionalArea, Long personId, String messageStructure) {
        return Optional.ofNullable(functionalArea)
                .map(area -> getFunctionalAreaResponsiveMap(area, personId))
                .map(response -> Optional.ofNullable(response)
                        .map(res -> res.getOrDefault(functionalArea, Collections.emptyMap()))
                        .map(allMap -> Optional.ofNullable(allMap.get("all"))
                                .map(allObject -> (HashMap) allObject)
                                .map(messagesMap -> Optional.ofNullable(messagesMap.get(StringUtils.defaultIfBlank(messageStructure, ContentConstants.STRUCTURED_MESSAGES_KEY)))
                                        .map(messagesList -> ((List) messagesList).stream()
                                                .filter(Objects::nonNull)
                                                .map(obj -> (LinkedHashMap) ((LinkedHashMap) obj).get(ContentConstants.CONTENT))
                                                .filter(Objects::nonNull)
                                                .flatMap(map -> map.entrySet().stream())
                                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, LinkedHashMap::new)))
                                        .orElse(null))
                                .orElse(null))
                        .orElse(null))
                .orElse(null);
    }
}
