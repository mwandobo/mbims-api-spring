package com.mwalimubank.mbimsapi.features.common.generic_detail.dto;

import com.mwalimubank.mbimsapi.features.common.generic_detail.GenericDetailEntity;
import lombok.Data;

@Data
public class GenericDetailResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String approvalStatus;
    private String createdAt;
    private String updatedAt;

    public static GenericDetailResponseDTO fromEntity(GenericDetailEntity entity) {
        GenericDetailResponseDTO dto = new GenericDetailResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }
}
