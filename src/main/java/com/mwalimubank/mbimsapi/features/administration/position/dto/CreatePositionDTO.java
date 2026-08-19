package com.mwalimubank.mbimsapi.features.administration.position.dto;

import lombok.Data;

@Data
public class CreatePositionDTO {
    private String name;
    private String description;
    private Long departmentId;
}
