package com.mwalimubank.mbimsapi.features.transaction.dto;

import com.mwalimubank.mbimsapi.features.transaction.entity.TransactionEntity;
import lombok.Data;

@Data
public class TransactionResponseDTO {
    private long id;
    private String customerName;
    private String channel;
    private Long name;
    private String amount;
    private String Date;
    private String Status;
    private String createdAt;
    private String updatedAt;

    public static TransactionResponseDTO fromEntity(TransactionEntity entity) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
//        dto.setName(entity.getId());
//        dto.setId(entity.getId());
//        dto.setName(entity.getName());
//        dto.setDescription(entity.getDescription());
//        dto.setCreatedAt(entity.getTmstamp() != null ? entity.getTmstamp().toString() : null);
//        dto.setUpdatedAt(entity.getTmstamp() != null ? entity.getTmstamp().toString() : null);
        return dto;
    }
}
