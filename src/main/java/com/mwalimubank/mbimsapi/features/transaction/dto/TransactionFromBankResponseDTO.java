package com.mwalimubank.mbimsapi.features.transaction.dto;

import com.mwalimubank.mbimsapi.features.transaction.entity.TransactionFromBankEntity;
import lombok.Data;

@Data
public class TransactionFromBankResponseDTO {
    private long id;
    private String customerName;
    private String channel;
    private Long name;
    private String amount;
    private String Date;
    private String Status;
    private String createdAt;
    private String updatedAt;

    public static TransactionFromBankResponseDTO fromEntity(TransactionFromBankEntity entity) {
        TransactionFromBankResponseDTO dto = new TransactionFromBankResponseDTO();
//        dto.setName(entity.getId());
//        dto.setId(entity.getId());
//        dto.setName(entity.getName());
//        dto.setDescription(entity.getDescription());
//        dto.setCreatedAt(entity.getTmstamp() != null ? entity.getTmstamp().toString() : null);
//        dto.setUpdatedAt(entity.getTmstamp() != null ? entity.getTmstamp().toString() : null);
        return dto;
    }
}
