public Map<String, LinkedHashMap> getMessages(String functionalArea, Long personId, String messageStructure) {
    return Optional.ofNullable(getFunctionalAreaResponsiveMap(functionalArea, personId))
            .map(ThrowingFunction.unchecked(responseString -> new ObjectMapper().readValue(responseString, new TypeReference<Map<String, Object>>() {})))
            .map(response -> response.getOrDefault(functionalArea, Collections.emptyMap()))
            .map(allMap -> (Map<String, Object>) allMap.getOrDefault("all", Collections.emptyMap()))
            .map(messagesMap -> (List<LinkedHashMap<String, Object>>) messagesMap.getOrDefault(StringUtils.defaultIfBlank(messageStructure, ContentConstants.STRUCTURED_MESSAGES_KEY), Collections.emptyList()))
            .map(messagesList -> messagesList.stream()
                    .map(msg -> (LinkedHashMap<String, Object>) msg.get(ContentConstants.CONTENT))
                    .filter(Objects::nonNull)
                    .reduce(new LinkedHashMap<>(), (result, msgContent) -> {
                        result.putAll(msgContent);
                        return result;
                    }))
            .filter(contentMap -> !contentMap.isEmpty())
            .orElse(null);
}
