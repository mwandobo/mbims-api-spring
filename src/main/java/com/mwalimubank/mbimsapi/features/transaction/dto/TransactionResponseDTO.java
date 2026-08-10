package com.mwalimubank.mbimsapi.features.transaction.dto;

import com.mwalimubank.mbimsapi.features.transaction.entity.TransactionEntity;
import lombok.Data;

@Data
public class TransactionResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String approvalStatus;
    private String createdAt;
    private String updatedAt;

    public static TransactionResponseDTO fromEntity(TransactionEntity entity) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }
}
