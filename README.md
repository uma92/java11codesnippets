private Map<String, Map<String, Object>> buildRequestAndHandleResponse(String apiType, String contentId, String functionalArea,
                                                                        Long personId, Long memberId, boolean segmentRequest) {
    Assert.notNull(functionalArea, ContentConstants.FUNCTIONAL_AREA_CANNOT_NULL);
    FunctionalArea fa = getFunctionalAreaFromConfig(functionalArea, segmentRequest);

    String cacheKey = buildCmsRequestCacheKey(apiType, contentId, functionalArea, personId, memberId, segmentRequest);
    Map<String, Map<String, Object>> targetMap = getCmsCache().computeIfAbsent(cacheKey, k -> new HashMap<>());

    try {
        if (targetMap.isEmpty() && fa != null) {
            targetMap.put(functionalArea, new HashMap<>());
            fa.getTargets().forEach((key, t) -> {
                String cmsRequest = createCmsRequest(apiType, contentId, personId, memberId, t);
                LOGGER.trace(ContentConstants.MAKING_CMS_REQUEST, cmsRequest);

                String cmsResponse = (AppConfigServiceImpl.isNewMBAEnabled() && isBenefitsApiType(t)) ?
                        fetchCmsResponseWithInputStream(cmsRequest) :
                        fetchCmsResponse(cmsRequest);

                Optional.ofNullable(cmsResponse)
                        .filter(StringUtils::isNotEmpty)
                        .map(response -> segmentRequest ? handleSegmentRequest(response) : doResponseHandling(response, t, key))
                        .ifPresent(responseMap -> {
                            targetMap.get(functionalArea).putAll(responseMap);
                            getCmsCache().put(cacheKey, targetMap);
                        });
            });
        }
    } catch (Exception e) {
        LOGGER.error("Could not handle CMS request", e);
        targetMap = null;
    }

    return targetMap;
}

private boolean isBenefitsApiType(Target t) {
    return t != null && t.getRequestOptions().size() > 0 &&
            StringUtils.isNotEmpty(t.getRequestOptions().get(0).getValue()) &&
            StringUtils.equalsIgnoreCase(t.getRequestOptions().get(0).getValue(), ApiEndpointMapImpl.BENEFITS_API_TYPE);
}

private String fetchCmsResponseWithInputStream(String cmsRequest) {
    try (InputStream inputStream = FileUtils.getInputStreamObject(cmsRequest, getDataPowerUser(), getDataPowerPass())) {
        return Optional.ofNullable(inputStream).map(IOUtils::toString).orElse(null);
    } catch (IOException e) {
        LOGGER.error("Error fetching CMS response with InputStream", e);
        return null;
    }
}
