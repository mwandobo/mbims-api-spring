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


@Data
public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private String staffNo;
    private String unit;
    private String department;
    private String position;
    private String email;
    private String createdAt;
    private String approvalStatus;
    private String gender;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static EmployeeResponseDTO fromEntity(EmployeeEntity entity) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setStaffNo(entity.getStaffNo());
        dto.setEmail(entity.getEmail());
        dto.setUnit(
                Optional.ofNullable(entity.getUnit())
                        .map(UnitEntity::getName)
                        .orElse(null)
        );
        dto.setDepartment(
                Optional.ofNullable(entity.getDepartment())
                        .map(DepartmentEntity::getName)
                        .orElse(null)
        );
        dto.setPosition(
                Optional.ofNullable(entity.getPosition())
                        .map(PositionEntity::getName)
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

}
