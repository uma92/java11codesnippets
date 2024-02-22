 List<Map<String, Object>> processedFilesList = Optional.ofNullable(targetMap.get(ContentConstants.COVERAGES_FUNCTIONAL_AREA))
            .map(coveragesMap -> (Map<String, Object>) coveragesMap.get(ContentConstants.PLANDOCS))
            .map(plandocsMap -> formatedPlanDocs((List<Map<String, Object>>) plandocsMap.get(ContentConstants.FILES)))
            .map(filesList -> filesList.stream()
                    .map(file -> {
                        Map<String, Object> processedFilesMap = new HashMap<>();
                        processedFilesMap.put(ContentConstants.ID, file.get(ContentConstants.ID));
                        processedFilesMap.put(ContentConstants.CONTENT_SUB_TYPE, file.get(ContentConstants.CONTENT_SUB_TYPE));
                        processedFilesMap.put(ContentConstants.TITLE, file.get(ContentConstants.TITLE));
                        processedFilesMap.put(ContentConstants.PATH_KEY, file.get(ContentConstants.PATH_KEY));

                        Map<String, Integer> sortOrderMap = new HashMap<>();
                        sortOrderMap.put(ContentConstants.SBC, 1);
                        sortOrderMap.put(ContentConstants.ANOCLETTER, 2);
                        sortOrderMap.put(ContentConstants.EOC, 3);
                        sortOrderMap.put(ContentConstants.BENEFITCHART, 4);
                        sortOrderMap.put(ContentConstants.BENEFITSATAGLANCE, 5);
                        sortOrderMap.put(ContentConstants.FORMULARY, 6);
                        sortOrderMap.put(ContentConstants.RESOURCEGUIDE, 7);
                        sortOrderMap.put(ContentConstants.PROVIDERDIRECTORY, 8);
                        sortOrderMap.put(ContentConstants.PHARMACYDIRECTORY, 9);
                        sortOrderMap.put(ContentConstants.PROVIDERPHARMACYDIRECTORY, 10);

                        processedFilesMap.put(ContentConstants.SORTORDER, sortOrderMap.get(file.get(ContentConstants.CONTENT_SUB_TYPE)));
                        return processedFilesMap;
                    })
                    .collect(Collectors.toList()))
            .orElse(Collections.emptyList());

    Map<String, List<Map<String, Object>>> simplifiedMap = Collections.singletonMap(ContentConstants.PLANDOCS, processedFilesList);

    try {
        return objectMapper.writeValueAsString(simplifiedMap);
    } catch (IOException e) {
        return null;
    }
}
