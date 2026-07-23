package com.mwalimubank.mbimsapi.features.administration.unit.dto;

import com.mwalimubank.mbimsapi.features.administration.unit.UnitEntity;
import lombok.Data;

@Data
public class UnitResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String manager;
    private String code;
    private String approvalStatus;
    private String createdAt;
    private String updatedAt;

    public static UnitResponseDTO fromEntity(UnitEntity entity) {
        UnitResponseDTO dto = new UnitResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }
}
