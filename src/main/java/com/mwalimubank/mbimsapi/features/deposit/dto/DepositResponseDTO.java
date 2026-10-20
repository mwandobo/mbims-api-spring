package com.mwalimubank.mbimsapi.features.deposit.dto;

import com.mwalimubank.mbimsapi.features.deposit.DepositEntity;
import com.mwalimubank.mbimsapi.core.utils.DateFormatterUtil;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositResponseDTO {
    private Long id;
    private String name;
    private Integer unitId;
    private BigDecimal amount;
    private String customerId;
    private String customerName;
    private String transactionDate;
    private String transactionType;
    private String transactionChannelType;
    private String ledgerCode;
    private String ledgerDescription;
    private String justification;
    private String productName;
    private String approvalStatus;
    private String createdAt;
    private String updatedAt;

    public static DepositResponseDTO fromEntity(DepositEntity entity) {
        DepositResponseDTO dto = new DepositResponseDTO();
        dto.setId(entity.getId());
        if (entity.getCustomer() != null) {
            dto.setCustomerName(entity.getCustomer().getName());
        } else {
            dto.setCustomerName(null); // or "Unknown"
        }
        dto.setTransactionDate(entity.getTmstamp().toString());
        dto.setTransactionType("Withdraw");
        dto.setTransactionChannelType("Mobile");
        dto.setLedgerCode(entity.getGlAccountCode());
        dto.setLedgerCode(entity.getGlAccountCode());
        dto.setJustification(entity.getJustification());
        dto.setAmount(entity.getDcAmount());

        dto.setProductName("Some Product");
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }
}
