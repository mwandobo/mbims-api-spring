package com.mwalimubank.mbimsapi.features.approval.dto;

import com.mwalimubank.mbimsapi.features.approval.enums.StatusEnum;
import lombok.Data;

@Data
public class UserApprovalRequestDTO {
    private String name;
    private String description;
    private String entityName;
    private Long sysApprovalId;
    private StatusEnum status;
}
