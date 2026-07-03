package com.mwalimubank.mbimsapi.features.performance.dto;

import com.mwalimubank.mbimsapi.features.performance.entities.PerformanceEntity;
import lombok.Data;

@Data
public class PerformanceResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String approvalStatus;
    private String createdAt;
    private String updatedAt;

    public static PerformanceResponseDTO fromEntity(PerformanceEntity entity) {
        PerformanceResponseDTO dto = new PerformanceResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }
}
