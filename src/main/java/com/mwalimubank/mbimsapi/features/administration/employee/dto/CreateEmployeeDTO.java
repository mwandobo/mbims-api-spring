package com.mwalimubank.mbimsapi.features.administration.employee.dto;

import lombok.Data;

@Data
public class CreateEmployeeDTO {
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private Long unitId;
    private String departmentId;
}
