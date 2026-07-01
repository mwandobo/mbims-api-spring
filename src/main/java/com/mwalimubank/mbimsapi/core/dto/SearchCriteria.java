package com.mwalimubank.mbimsapi.core.dto;

public record SearchCriteria(
        String key,
        String operation,
        Object value
) {}