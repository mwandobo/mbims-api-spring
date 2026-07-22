package com.mwalimubank.mbimsapi.features.transaction.dto;

import lombok.Data;

@Data
public class CreateTransactionDTO {
    private String name;
    private String description;
}
