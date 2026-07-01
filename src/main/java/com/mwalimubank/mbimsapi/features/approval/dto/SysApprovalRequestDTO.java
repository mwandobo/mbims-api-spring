package com.mwalimubank.mbimsapi.features.approval.dto;

import com.mwalimubank.mbimsapi.features.approval.enums.StatusEnum;
import lombok.Data;

@Data
public class SysApprovalRequestDTO {
    private String name;
    private String description;
    private String entityName;
    private StatusEnum status;
}
