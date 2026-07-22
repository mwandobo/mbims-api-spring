package com.mwalimubank.mbimsapi.features.administration.employee.dto;

import com.mwalimubank.mbimsapi.features.administration.employee.EmployeeEntity;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Stream;

@Data
public class EmployeeResponseDTO {
    private String id;
    private String name;
    private String createdAt;
    private String gender;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static EmployeeResponseDTO fromEntity(EmployeeEntity entity) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(entity.getId());

        dto.setName(
                String.join(" ",
                        Stream.of(
                                        entity.getFirstName(),
                                        entity.getFatherName(),
                                        entity.getLastName()
                                )
                                .filter(Objects::nonNull)
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .toArray(String[]::new)
                )
        );
        dto.setCreatedAt(
                entity.getCreatedAt() != null
                        ? entity.getCreatedAt().format(FORMATTER)
                        : null
        );
        dto.setGender(getGender(entity.getSex()));
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
