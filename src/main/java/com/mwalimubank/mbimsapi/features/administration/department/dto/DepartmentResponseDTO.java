package com.mwalimubank.mbimsapi.features.administration.department.dto;

import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import java.time.format.DateTimeFormatter;
import lombok.Data;

@Data
public class DepartmentResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String approvalStatus;
    private String createdAt;
    private String updatedAt;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static  DepartmentResponseDTO fromEntity( DepartmentEntity department) {
            DepartmentResponseDTO dto = new DepartmentResponseDTO();
            dto.setId(department.getId());
            dto.setName(department.getName());
            dto.setDescription(department.getDescription());
            dto.setUpdatedAt(department.getUpdatedAt() != null ? department.getUpdatedAt().toString() : null);
            dto.setCreatedAt(department.getCreatedAt() != null ? department.getCreatedAt().toString() : null);
            return dto;
        }
}
