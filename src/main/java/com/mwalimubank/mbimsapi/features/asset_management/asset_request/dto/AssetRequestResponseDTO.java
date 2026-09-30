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
    private Integer requestedItemsQuantity;
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
        if (entity.getItems() != null && !entity.getItems().isEmpty()) {
            // Option 1: Total quantity (sum of all item quantities)
            int totalQuantity = entity.getItems().stream()
                    .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                    .sum();
            dto.setRequestedItemsQuantity(totalQuantity);

            // Option 2: Just the number of items (uncomment if you prefer this)
            // dto.setRequestedItemsQuantity(entity.getItems().size());
        } else {
            dto.setRequestedItemsQuantity(0);
        }
        dto.setStatusLabel(mapStatusLabel(entity.getStatus()));
        if (entity.getCreatedBy() != null) {
            dto.setCreatedById(entity.getCreatedBy().getId());
            dto.setCreatedByName(entity.getCreatedBy().getName());
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