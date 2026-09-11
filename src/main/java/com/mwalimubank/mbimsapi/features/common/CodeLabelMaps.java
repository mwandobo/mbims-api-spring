package com.mwalimubank.mbimsapi.features.common;

import java.util.*;
import java.util.stream.Collectors;

public final class CodeLabelMaps {

    private CodeLabelMaps() {}

    public static final Map<String, String> CUST_TYPE = Map.of(
            "1", "Individual",
            "2", "Corporate",
            "3", "Corporate"
    );

    public static final Map<String, String> SEX = Map.of(
            "M", "Male",
            "F", "Female"
    );

    /** "indi", "vidual", "individual" → ["1"] */
    public static List<String> codesMatching(Map<String, String> codeToLabel, String search) {
        if (search == null || search.isBlank()) {
            return List.of();
        }
        String q = search.trim().toLowerCase();
        return codeToLabel.entrySet().stream()
                .filter(e -> e.getValue().toLowerCase().contains(q))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}