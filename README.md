private Map<String, Object> doResponseHandling(String jsonString, Target target, String targetKey) {
    try {
        Map<String, Object> responseMap = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});

        if (!responseMap.isEmpty()) {
            expireTagCache();

            ResponseOptions responseOptions = target.getResponseOptions();
            Map<String, List<Object>> builtContentTypesMap = responseMap.entrySet().stream()
                    .filter(entry -> entry.getValue() instanceof List)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> ((List<Map<String, Object>>) entry.getValue()).stream()
                                    .flatMap(contentObject -> handleContentObject(contentObject, target))
                                    .collect(Collectors.toList())
                    ));

            if (responseOptions.getDedupe()) {
                builtContentTypesMap.replaceAll((key, value) -> dedupeContent(value));
            }

            return Collections.singletonMap(targetKey, builtContentTypesMap);
        }
    } catch (IOException e) {
        LOGGER.error("Error handling response properly", e);
    }

    return null;
}

private Stream<Object> handleContentObject(Map<String, Object> contentObject, Target target) {
    List<Map<String, Object>> contentList = (List<Map<String, Object>>) contentObject.get(ContentConstants.CONTENT);

    Map<String, Object> targeting = (Map<String, Object>) contentObject.get(ContentConstants.TARGETING);
    List<String> funcList = Optional.ofNullable(targeting.get(ContentConstants.FUNC_KEY))
            .map(func -> (List<String>) func)
            .orElse(Collections.emptyList());

    List<String> targetLocationList = Optional.ofNullable(targeting.get(ContentConstants.TARGET_LOCATION))
            .map(targetLocation -> (List<String>) targetLocation)
            .orElse(Arrays.asList(ContentConstants.ALL_LOWER));

    List<String> segmentList = Optional.ofNullable(targeting.get(ContentConstants.SEGMENT_KEY))
            .map(segment -> (List<String>) segment)
            .orElse(Collections.emptyList());

    String apiType = cmsRequestBuilder.getApiType(target.getRequestOptions());

    return contentList.stream()
            .map(content -> hoistAttributes(cmsParser.parse(apiType, content)))
            .peek(content -> {
                content.put(ContentConstants.FUNC_KEY, funcList);
                content.put(ContentConstants.TARGET_LOCATION, targetLocationList);
                content.put(ContentConstants.SEGMENT_KEY, segmentList);
                content.put(ContentConstants.CONTENT_TYPE, contentObject.getKey());
            });
}

private List<Object> dedupeContent(List<Object> contentList) {
    // Deduplication logic here
    // This method should be implemented based on your deduplication requirements
    return contentList;
}
