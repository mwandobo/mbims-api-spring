package com.mwalimubank.mbimsapi.features.approval.dto;

import com.mwalimubank.mbimsapi.features.approval.enums.StatusEnum;
import lombok.Data;

@Data
public class ApprovalLevelRequestDTO {
    private String name;
    private String description;
    private String level;
    private Long roleId;
    private Long userId;
    private StatusEnum status;
}
