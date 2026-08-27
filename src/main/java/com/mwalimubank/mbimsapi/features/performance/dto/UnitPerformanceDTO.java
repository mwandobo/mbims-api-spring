package com.mwalimubank.mbimsapi.features.performance.dto;

import lombok.Data;

@Data
public class UnitPerformanceDTO {
    private Long unitId;
    private String unitName;
    private Long totalCustomers;
    private Long active;
    private Long dormant;
    private Long closed;
}