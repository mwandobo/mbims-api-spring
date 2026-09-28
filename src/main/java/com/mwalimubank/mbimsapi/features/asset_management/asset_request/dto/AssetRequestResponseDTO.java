package com.mwalimubank.mbimsapi.features.asset_management.asset_request.dto;

import com.mwalimubank.mbimsapi.core.utils.DateFormatterUtil;
import com.mwalimubank.mbimsapi.features.asset_management.asset_request.AssetRequestEntity;
import lombok.Data;

@Data
public class AssetRequestResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Integer status;
    private String statusLabel;      // Human readable

    // Created By info
    private Long createdById;
    private String createdByName;

    private String approvalStatus;
    private String createdAt;
    private String updatedAt;

    public static AssetRequestResponseDTO fromEntity(AssetRequestEntity entity) {
        AssetRequestResponseDTO dto = new AssetRequestResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setStatusLabel(mapStatusLabel(entity.getStatus()));
        if (entity.getCreatedBy() != null) {
            dto.setCreatedById(entity.getCreatedBy().getId());
            // Adjust according to your UserEntity fields
            dto.setCreatedByName(entity.getCreatedBy().getName());
            // or getFirstName() + " " + getLastName()
        }

        dto.setCreatedAt(DateFormatterUtil.format(entity.getCreatedAt()));
        dto.setUpdatedAt(DateFormatterUtil.format(entity.getUpdatedAt()));
        return dto;
    }

    private static String mapStatusLabel(Integer status) {
        if (status == null) return "Unknown";
        return switch (status) {
            case 0 -> "Draft";
            case 1 -> "Submitted";
            default -> "Unknown";
        };
    }
}