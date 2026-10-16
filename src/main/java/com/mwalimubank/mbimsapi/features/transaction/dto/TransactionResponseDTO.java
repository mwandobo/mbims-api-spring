package com.mwalimubank.mbimsapi.features.transaction.dto;

import com.mwalimubank.mbimsapi.features.transaction.entity.TransactionEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionResponseDTO {
    private Long id;
    private String name;
    private Integer unitId;
    private String customerId;
    private String customerName;
    private String transactionDate;
    private String transactionType;
    private BigDecimal amount;
    private String transactionChannelType;
    private String justification;
    private String productName;
    private String approvalStatus;
    private String createdAt;
    private String updatedAt;

    public static TransactionResponseDTO fromEntity(TransactionEntity entity) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(entity.getId());
        dto.setUnitId(entity.getUnitId());
//        dto.setCustomerId(entity.getCustomerId());
//        dto.setCustomerName(entity.getCustomer().getName());

        // Null-safe customer name
        if (entity.getCustomer() != null) {
            dto.setCustomerName(entity.getCustomer().getName());
        } else {
            dto.setCustomerName(null); // or "Unknown"
        }
        dto.setTransactionDate(entity.getTmstamp().toString());
        dto.setTransactionType("Withdraw");
        dto.setTransactionChannelType("Mobile");
        dto.setJustification(entity.getJustification());
        dto.setAmount(entity.getDcAmount());
        dto.setProductName("Some Product");
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }
}
