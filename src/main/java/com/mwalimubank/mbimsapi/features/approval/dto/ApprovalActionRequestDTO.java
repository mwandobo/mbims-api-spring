package com.mwalimubank.mbimsapi.features.approval.dto;

import com.mwalimubank.mbimsapi.features.approval.enums.ApprovalActionEnum;
import com.mwalimubank.mbimsapi.features.approval.enums.StatusEnum;
import lombok.Data;

import java.util.List;

@Data
public class ApprovalActionRequestDTO {
    private String name;
    private ApprovalActionEnum action;
    private String entityName;
    private List<ApprovedItemDTO> extraData1;
    private Long entityCreatorId;
    private Long entityId;
    private String description;
    private String redirectUrl;
    private Long approvalLevelId;
    private StatusEnum status;
}
