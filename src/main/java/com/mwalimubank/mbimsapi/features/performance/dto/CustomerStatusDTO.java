package com.mwalimubank.mbimsapi.features.performance.dto;

import lombok.Data;

@Data
public class CustomerStatusDTO {
    private long active;
    private long closed;
    private long dormant;
}