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
                                                .map(obj -> (LinkedHashMap) obj)
                                                .filter(messageMap -> messageMap.get(ContentConstants.CONTENT) != null)
                                                .map(messageMap -> (LinkedHashMap) messageMap.get(ContentConstants.CONTENT))
                                                .collect(Collectors.toMap(Map::entrySet, LinkedHashMap::putAll, (m1, m2) -> m1)))
                                        .orElse(null))
                                .orElse(null))
                        .orElse(null))
                .orElse(null);
    }
