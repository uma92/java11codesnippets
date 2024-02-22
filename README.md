private Map<String, Object> hoistAttributes(Map<String, Object> content) {
    List<String> attrsToHoist = Arrays.asList(
            ContentConstants.ACTIVE_DATE_END,
            ContentConstants.ACTIVE_DATE_START,
            "alt",
            ContentConstants.CONTENT_TYPE,
            ContentConstants.CONTENT_SUB_TYPE,
            "description",
            ContentConstants.MODIFIED_DATE_KEY,
            "path",
            "sortpriority",
            ContentConstants.TITLE,
            "aria_label"
    );

    return Optional.ofNullable(content)
            .map(c -> (Map<String, Object>) c.get(ContentConstants.CONTENT))
            .filter(contentObj -> contentObj != null && !contentObj.isEmpty())
            .map(contentObj ->
                    attrsToHoist.stream()
                            .filter(attrToHoist -> contentObj.containsKey(attrToHoist))
                            .forEach(attrToHoist -> content.put(attrToHoist, contentObj.get(attrToHoist)))
            )
            .orElse(content);
}
