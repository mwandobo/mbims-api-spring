package com.mwalimubank.mbimsapi.features.performance.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomerStatsResponseDTO {
    private long  totalAllCustomers;
    private CustomerStatusDTO allCustomersAttrs;
    private long  totalIndividualCustomers;
    private CustomerStatusDTO individualCustomersAttrs;
    private long  totalCorporateCustomers;
    private CustomerStatusDTO corporateCustomersAttrs;
}

