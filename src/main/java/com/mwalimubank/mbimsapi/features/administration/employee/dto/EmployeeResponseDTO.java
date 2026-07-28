package com.mwalimubank.mbimsapi.features.administration.employee.dto;

import com.mwalimubank.mbimsapi.features.administration.employee.entity.EmployeeEntity;
import lombok.Data;
import java.time.format.DateTimeFormatter;


@Data
public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private String createdAt;
    private String approvalStatus;
    private String gender;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static EmployeeResponseDTO fromEntity(EmployeeEntity entity) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCreatedAt( entity.getCreatedAt().toString() );
        dto.setGender(entity.getGender());
        return dto;
    }


}
