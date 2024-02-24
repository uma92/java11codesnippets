 public Map<String, Object> getParsedContentBySlug(String slug) {
        String cmsResponse = String.valueOf(cmsConfigService.getConfigAsStream(buildTuple(1, List.of(Pair.of(1,
                               CmsConstants.SLUG, slug)))).map(inputStream -> new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                               .lines().collect(Collectors.joining("\n"))));
        log.debug("Making CMS request for parsed content by slug with url: [{}]", cmsResponse);

        try {

            Map<String, Object> responseMap = objectMapper.readValue(cmsResponse, Map.class);

            for (String contentTypeKey : responseMap.keySet()) {

                List<Map<String, Object>> contentTypeList = (List<Map<String, Object>>) responseMap.get(contentTypeKey);

                for (Map<String, Object> contentType : contentTypeList) {

                    List<Map<String, Object>> contentPieces = (List<Map<String, Object>>) contentType.get(CONTENT);
                    for (Map<String, Object> content : contentPieces) {
                        return cmsParser.parse(ApiType.CMS.getType(), content);
                    }
                }
            }


        } catch (IOException e) {
            log.error("Error parsing request for: [{}]", cmsResponse);
        }


        return null;

    }
