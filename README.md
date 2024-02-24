public Map<String, Object> getParsedContentBySlug(String slug) {
    try {
        return cmsConfigService.getConfigAsStream(buildTuple(1, List.of(Pair.of(1, CmsConstants.SLUG, slug))))
                .map(inputStream -> new BufferedReader(new InputStreamReader(inputStream,StandardCharsets.UTF_8)))
                .map(reader -> reader.lines().collect(Collectors.joining())) // Read & join lines
                .map(objectMapper::readValue) // Parse JSON string
                .flatMap(map -> map.entrySet().stream()
                        .flatMap(entry -> (List<Map<String, Object>>) entry.getValue().get(CONTENT).stream())
                        .map(content -> cmsParser.parse(ApiType.CMS.getType(), content))
                )
                .findFirst()
                .orElse(null);
    } catch (IOException e) {
        log.error("Error parsing request for: [{}]", e.getMessage());
        return null;
    }
}

===============================
public Map<String, Object> getParsedContentBySlug(String slug) {
    String cmsResponse = String.valueOf(cmsConfigService.getConfigAsStream(buildTuple(1, List.of(Pair.of(1, CmsConstants.SLUG, slug))))
            .map(inputStream -> new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"))));

    log.debug("Making CMS request for parsed content by slug with url: [{}]", cmsResponse);

    try {
        return objectMapper.readValue(cmsResponse, Map.class).entrySet().stream()
                .flatMap(entry -> ((List<Map<String, Object>>) entry.getValue()).stream()
                        .flatMap(contentType -> ((List<Map<String, Object>>) contentType.get(CONTENT)).stream()
                                .findFirst()
                                .map(content -> cmsParser.parse(ApiType.CMS.getType(), content))
                                .stream()))
                .findFirst()
                .orElse(null);
    } catch (IOException e) {
        log.error("Error parsing request for: [{}]", cmsResponse);
    }

    return null;
}
