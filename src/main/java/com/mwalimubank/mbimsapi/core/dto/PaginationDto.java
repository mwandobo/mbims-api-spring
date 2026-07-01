package com.mwalimubank.mbimsapi.core.dto;

public record PaginationDto(
        long total,
        int page,
        int limit,
        int totalPages
) {}