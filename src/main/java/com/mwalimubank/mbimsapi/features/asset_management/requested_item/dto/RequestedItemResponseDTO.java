package com.mwalimubank.mbimsapi.features.asset_management.requested_item.dto;

import com.mwalimubank.mbimsapi.core.utils.DateFormatterUtil;
import com.mwalimubank.mbimsapi.features.asset_management.requested_item.RequestedItemEntity;
import lombok.Data;

@Data
public class RequestedItemResponseDTO {

    private Long id;
    private Integer quantity;

    // Asset info
    private Long assetId;
    private String assetName;
    private String categoryName;

    // Request info
    private Long requestId;
    private String requestName;

    // Used by PagedQueryService / ApprovalStatusUtil
    // Approval & audit
    private String approvalStatus;
    private String createdAt;
    private String updatedAt;

    public static RequestedItemResponseDTO fromEntity(RequestedItemEntity entity) {
        RequestedItemResponseDTO dto = new RequestedItemResponseDTO();

        dto.setId(entity.getId());
        dto.setQuantity(entity.getQuantity());

        // Asset
        if (entity.getAsset() != null) {
            dto.setAssetId(entity.getAsset().getId());
            dto.setAssetName(entity.getAsset().getName());

            if (entity.getAsset().getAssetCategory() != null) {
                dto.setCategoryName(entity.getAsset().getAssetCategory().getName());
            }
        }

        // Request
        if (entity.getRequest() != null) {
            dto.setRequestId(entity.getRequest().getId());
            dto.setRequestName(entity.getRequest().getName());
        }

        // Timestamps
        dto.setCreatedAt(DateFormatterUtil.format(entity.getCreatedAt()));
        dto.setUpdatedAt(DateFormatterUtil.format(entity.getUpdatedAt()));

        return dto;
    }

}