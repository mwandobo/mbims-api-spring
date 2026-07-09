package com.mwalimubank.mbimsapi.features.common.dto;

import com.mwalimubank.mbimsapi.features.common.entity.OtherIdEntity;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class OtherIdDTO {

    private Integer customerId;
    private Short serialNo;
    private String idNo;
    private String issueAuthority;
    private String issueDate;
    private String expiryDate;
    private String mainFlag;
    private String idType;
    private String issuedStatus;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public static OtherIdDTO fromEntity(OtherIdEntity entity) {
        OtherIdDTO dto = new OtherIdDTO();

        dto.setCustomerId(entity.getId().getFkCustomerCustId());
        dto.setSerialNo(entity.getId().getSerialNo());

        dto.setIdNo(entity.getIdNo());
        dto.setIssueAuthority(entity.getIssueAuthority());

        dto.setIssueDate(
                entity.getIssueDate() != null
                        ? entity.getIssueDate().format(FORMATTER)
                        : null
        );

        dto.setExpiryDate(
                entity.getExpiryDate() != null
                        ? entity.getExpiryDate().format(FORMATTER)
                        : null
        );

        dto.setMainFlag(entity.getMainFlag());

        dto.setIdType(entity.getFkGhHasType());
        dto.setIssuedStatus(entity.getFkGhHasBeenIssu());

        return dto;
    }
}