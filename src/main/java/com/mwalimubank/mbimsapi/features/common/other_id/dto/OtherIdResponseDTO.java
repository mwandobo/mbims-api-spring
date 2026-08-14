package com.mwalimubank.mbimsapi.features.common.other_id.dto;

import com.mwalimubank.mbimsapi.features.common.other_id.OtherIdEntity;
import lombok.Data;

@Data
public class OtherIdResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String approvalStatus;
    private String createdAt;
    private String updatedAt;

    public static OtherIdResponseDTO fromEntity(OtherIdEntity entity) {
        OtherIdResponseDTO dto = new OtherIdResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }
}
