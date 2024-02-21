@Override
public Map<String, Object> getJsonContentMap(String functionalArea, Long personId, Long memberId) {
    return parseJsonContent(getJsonContentString(functionalArea, personId, memberId));
}

@Override
public String getJsonContentString(String functionalArea, Long personId, Long memberId) {
    Assert.notNull(memberId, "memberId cannot be null");
    Assert.notNull(personId, "personId cannot be null");

    return doContentFetch(functionalArea, personId, memberId);
}

private String doContentFetch(String functionalArea, Long personId, Long memberId) {
    String cacheKey = buildCmsRequestCacheKey(ApiEndpointMapImpl.CMS_API_TYPE, null, functionalArea, personId, memberId, false);
    return Optional.ofNullable(getCmsCache().get(cacheKey))
            .orElseGet(() -> fetchAndCacheContent(functionalArea, personId, memberId))
            .map(this::convertToJsonString)
            .orElse(null);
}

private Optional<Map<String, Map<String, Object>>> fetchAndCacheContent(String functionalArea, Long personId, Long memberId) {
    Map<String, Map<String, Object>> targetMap = new HashMap<>();
    targetMap.put(functionalArea, new HashMap<>());

    getFunctionalAreaFromConfig(functionalArea, false)
            .map(FunctionalArea::getTargets)
            .ifPresent(targets -> targets.forEach((key, target) -> {
                String cmsRequest = createCmsRequest(ApiEndpointMapImpl.CMS_API_TYPE, null, personId, memberId, target);
                LOGGER.trace(ContentConstants.MAKING_CMS_REQUEST, cmsRequest);

                String cmsResponse = fetchCmsResponse(target, cmsRequest);
                Optional.ofNullable(cmsResponse)
                        .filter(response -> !response.isEmpty())
                        .ifPresent(response -> {
                            Map<String, Object> responseMap = Optional.ofNullable(target)
                                    .map(t -> doResponseHandling(response, t, key))
                                    .orElse(parseJsonContent(response));

                            Optional.ofNullable(responseMap)
                                    .filter(map -> !map.isEmpty())
                                    .ifPresent(map -> {
                                        targetMap.get(functionalArea).putAll(map);
                                        getCmsCache().put(buildCmsRequestCacheKey(ApiEndpointMapImpl.CMS_API_TYPE, null, functionalArea, personId, memberId, false), targetMap);
                                    });
                        });
            }));

    return targetMap.isEmpty() ? Optional.empty() : Optional.of(targetMap);
}

private String fetchCmsResponse(Target target, String cmsRequest) {
    return Optional.ofNullable(target)
            .filter(t -> AppConfigServiceImpl.isNewMBAEnabled() && t.getRequestOptions().size() > 0 && !StringUtils.isEmpty(t.getRequestOptions().get(0).getValue()) && StringUtils.equalsIgnoreCase(t.getRequestOptions().get(0).getValue(), ApiEndpointMapImpl.BENEFITS_API_TYPE))
            .map(t -> {
                try (InputStream inputStream = FileUtils.getInputStreamObject(cmsRequest, getDataPowerUser(), getDataPowerPass())) {
                    return Optional.ofNullable(inputStream)
                            .map(IOUtils::toString)
                            .orElse(null);
                } catch (IOException e) {
                    return null;
                }
            })
            .orElseGet(() -> fetchCmsResponse(cmsRequest));
}

private String fetchCmsResponse(String cmsRequest) {
    return fetchCmsResponse(cmsRequest);
}

private String convertToJsonString(Map<String, ?> map) {
    try {
        return objectMapper.writeValueAsString(map);
    } catch (IOException e) {
        return null;
    }
}
