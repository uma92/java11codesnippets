return responseMap.values().stream()
                .filter(value -> value instanceof List)
                .flatMap(contentTypeList -> ((List<Map<String, Object>>) contentTypeList).stream())
                .map(contentType -> ((List<Map<String, Object>>) contentType.get(CONTENT)).stream())
                .flatMap(List::stream)
                .findFirst()
                .map(content -> cmsParser.parse(ApiType.CMS.getType(), content))
                .orElse(null);
