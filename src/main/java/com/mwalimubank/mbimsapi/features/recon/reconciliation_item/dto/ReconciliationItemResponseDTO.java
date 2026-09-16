package com.mwalimubank.mbimsapi.features.recon.reconciliation_item.dto;

import com.mwalimubank.mbimsapi.features.recon.reconciliation_item.ReconciliationItemEntity;
import com.mwalimubank.mbimsapi.core.utils.DateFormatterUtil;
import lombok.Data;

@Data
public class ReconciliationItemResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String approvalStatus;
    private String createdAt;
    private String updatedAt;

    public static ReconciliationItemResponseDTO fromEntity(ReconciliationItemEntity entity) {
        ReconciliationItemResponseDTO dto = new ReconciliationItemResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(DateFormatterUtil.format(entity.getCreatedAt()));
        dto.setUpdatedAt(DateFormatterUtil.format(entity.getUpdatedAt()));
        return dto;
    }
}
