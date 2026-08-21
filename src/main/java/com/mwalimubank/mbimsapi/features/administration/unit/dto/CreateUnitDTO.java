package com.mwalimubank.mbimsapi.features.administration.unit.dto;

import lombok.Data;

@Data
public class CreateUnitDTO {
    private String name;
    private String description;
    private Long managerId;
    private String code;
}
