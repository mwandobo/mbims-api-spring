package com.mwalimubank.mbimsapi.features.administration.employee.dto;

import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.administration.employee.entity.EmployeeEntity;
import com.mwalimubank.mbimsapi.features.administration.position.PositionEntity;
import com.mwalimubank.mbimsapi.features.administration.unit.UnitEntity;
import lombok.Data;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Data
public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private String firstName;
    private String middleName;
    private String LastName;
    private String staffNo;
    private String unitName;
    private Long unitId;
    private String departmentName;
    private Long departmentId;
    private String positionName;
    private Long positionId;
    private String email;
    private String mobilePhone;
    private String createdAt;
    private String approvalStatus;
    private String gender;
    private Boolean isCredentialShared;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static EmployeeResponseDTO fromEntity(EmployeeEntity entity) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(entity.getId());


        // Build full name safely and set it on the DTO
        String fullName = Stream.of(entity.getFirstName(), entity.getMiddleName(), entity.getLastName())
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .collect(Collectors.joining(" "));

        dto.setName(fullName.isBlank() ? null : fullName);
        dto.setFirstName(entity.getFirstName());
        dto.setMiddleName(entity.getMiddleName());
        dto.setLastName(entity.getLastName());
        dto.setStaffNo(entity.getStaffNo());
        dto.setMobilePhone(entity.getMobilePhone());
        dto.setIsCredentialShared(entity.getIsCredentialsShared());
        dto.setEmail(entity.getEmail());
        dto.setUnitName(
                Optional.ofNullable(entity.getUnit())
                        .map(UnitEntity::getName)
                        .orElse(null)
        );
        dto.setUnitId(
                Optional.ofNullable(entity.getUnit())
                        .map(UnitEntity::getId)
                        .orElse(null)
        );
        dto.setDepartmentName(
                Optional.ofNullable(entity.getDepartment())
                        .map(DepartmentEntity::getName)
                        .orElse(null)
        );
        dto.setDepartmentId(
                Optional.ofNullable(entity.getDepartment())
                        .map(DepartmentEntity::getId)
                        .orElse(null)
        );
        dto.setPositionName(
                Optional.ofNullable(entity.getPosition())
                        .map(PositionEntity::getName)
                        .orElse(null)
        );
        dto.setPositionId(
                Optional.ofNullable(entity.getPosition())
                        .map(PositionEntity::getId)
                        .orElse(null)
        );

        dto.setCreatedAt(format(entity.getCreatedAt()));
        dto.setGender(
                "M".equalsIgnoreCase(entity.getGender()) ? "Male" :
                        "F".equalsIgnoreCase(entity.getGender()) ? "Female" :
                                "Not Applicable"
        );
        return dto;
    }

    public static String format(Instant instant) {
        if (instant == null) return null;
        return instant.atZone(ZoneOffset.UTC)
                .toLocalDate()
                .format(FORMATTER);
    }

    private String buildFullName(String firstName, String middleName, String lastName) {
        return Stream.of(firstName, middleName, lastName)
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .collect(Collectors.joining(" "));
    }
}