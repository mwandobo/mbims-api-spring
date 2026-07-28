package com.mwalimubank.mbimsapi.features.administration.employee.dto;

import com.mwalimubank.mbimsapi.features.administration.employee.entity.EmployeeEntity;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Stream;

@Data
public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private String createdAt;
    private String gender;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static EmployeeResponseDTO fromEntity(EmployeeEntity entity) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(entity.getId());

        dto.setName(entity.getName());
        dto.setCreatedAt( entity.getCreatedAt().toString() );
        dto.setGender(getGender(entity.getGender()));
        return dto;
    }

    private static String getGender(String sex) {
        if (sex == null) {
            return "Not Applicable";
        }
        return switch (sex.trim().toUpperCase()) {
            case "M" -> "Male";
            case "F" -> "Female";
            default -> "Not Applicable";
        };
    }
}
