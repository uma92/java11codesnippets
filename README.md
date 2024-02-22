public Map<String, LinkedHashMap> getMessages(String functionalArea, Long personId, String messageStructure) {
    Map<String, Map<String, Object>> response = getFunctionalAreaResponsiveMap(functionalArea, personId);

    // Validate input parameters early and return null if required
    if (StringUtils.isEmpty(functionalArea) || response == null) {
        return null;
    }

    Map<String, Object> functionalAreaMap = response.get(functionalArea);
    String chosenStructure = StringUtils.isBlank(messageStructure) ? ContentConstants.STRUCTURED_MESSAGES_KEY : messageStructure;

    // Use Optional for concise handling of potential null values and default structure
    Optional<Map<String, Object>> messagesMap = Optional.ofNullable(functionalAreaMap)
        .map(m -> m.get(chosenStructure))
        .map(LinkedHashMap.class::cast);

    return messagesMap
        .map(Map::values)
        .stream()
        .flatMap(Collection::stream)
        .filter(Map.class::isInstance)
        .map(Map.class::cast)
        .filter(m -> m.get(ContentConstants.CONTENT) != null)
        .flatMap(m -> ((Map) m.get(ContentConstants.CONTENT)).entrySet().stream())
        .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);
}
