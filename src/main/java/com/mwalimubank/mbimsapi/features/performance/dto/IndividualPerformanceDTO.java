package com.mwalimubank.mbimsapi.features.performance.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class IndividualPerformanceDTO {
    private Long employeeId;
    private String name;
    private String role;              // position / job title
    private String unit;              // unit name
    private String staffNo;
    private Long customerOnboarding;
    private BigDecimal deposits;
    private BigDecimal loans;         // dummy for now
}