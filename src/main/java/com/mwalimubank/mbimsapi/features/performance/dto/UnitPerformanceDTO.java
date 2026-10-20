package com.mwalimubank.mbimsapi.features.performance.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UnitPerformanceDTO {
    private Long unitId;
    private String unitName;                 // frontend key: "unit"
    private String unitCode;                 // frontend key: "unit"
    private Long customerOnboarding;     // count of customers for the unit
    private BigDecimal deposits;         // sum of deposits for the unit
    private BigDecimal loans;            // dummy for now
    private BigDecimal expenses;         // dummy for now
}