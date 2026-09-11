package com.mwalimubank.mbimsapi.features.asset_management.asset.dto;

import com.mwalimubank.mbimsapi.features.asset_management.asset.AssetEntity;
import java.time.format.DateTimeFormatter;

import com.mwalimubank.mbimsapi.features.asset_management.assetcategory.dto.AssetCategoryResponseDTO;
import lombok.Data;

@Data
public class AssetResponseDTO {
    private Long id;
    private String name;
    private String description;
    private AssetCategoryResponseDTO assetCategory;
    private String assetCategoryName;
    private Long assetCategoryId;
    private String approvalStatus;
    private String createdAt;
    private String updatedAt;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static  AssetResponseDTO fromEntity( AssetEntity asset) {
            AssetResponseDTO dto = new AssetResponseDTO();
            dto.setId(asset.getId());
            dto.setName(asset.getName());
            dto.setDescription(asset.getDescription());
            dto.setAssetCategory(asset.getAssetCategory() != null ? AssetCategoryResponseDTO.fromEntity(asset.getAssetCategory()) : null);
            dto.setAssetCategoryName(asset.getAssetCategory() != null ? asset.getAssetCategory().getName() : null);
            dto.setAssetCategoryId(asset.getAssetCategory() != null ? asset.getAssetCategory().getId() : null);
            dto.setUpdatedAt(asset.getUpdatedAt() != null ? asset.getUpdatedAt().toString() : null);
            dto.setCreatedAt(asset.getCreatedAt() != null ? asset.getCreatedAt().toString() : null);
            return dto;
        }
}
