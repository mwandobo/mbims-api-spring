package com.mwalimubank.mbimsapi.features.performance.dto;

import lombok.Data;

@Data
public class CustomerStatsResponseDTO {
    private long totalCustomers;
    private long totalIndividualCustomers;
    private long totalCorporateCustomers;
}
