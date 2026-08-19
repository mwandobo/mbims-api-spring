package com.mwalimubank.mbimsapi.features.administration.position.dto;

import com.mwalimubank.mbimsapi.features.administration.department.dto.DepartmentResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.position.PositionEntity;
import lombok.Data;

@Data
public class PositionResponseDTO {
    private Long id;
    private String name;
    private String description;
    private DepartmentResponseDTO department;
    private String departmentName;
    private String approvalStatus;
    private String createdAt;
    private String updatedAt;

    public static PositionResponseDTO fromEntity(PositionEntity entity) {
        PositionResponseDTO dto = new PositionResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }
}
