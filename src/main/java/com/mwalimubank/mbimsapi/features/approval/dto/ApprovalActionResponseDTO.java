package com.mwalimubank.mbimsapi.features.approval.dto;

import com.mwalimubank.mbimsapi.core.utils.DateFormatterUtil;
import com.mwalimubank.mbimsapi.features.approval.entity.ApprovalActionEntity;
import com.mwalimubank.mbimsapi.features.approval.enums.ApprovalActionEnum;
import com.mwalimubank.mbimsapi.features.approval.enums.StatusEnum;
import lombok.Data;

@Data
public class ApprovalActionResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String approvalStatus;
    private String entityName;
    private Long sysApprovalId;
    private String approvalLevelName;
    private Long approvalLevelId;
    private ApprovalActionEnum action;
    private Long createdById;
    private String createdByName;
    private StatusEnum status;
    private String createdAt;
    private String updatedAt;

    public static ApprovalActionResponseDTO fromEntity(ApprovalActionEntity entity) {
        ApprovalActionResponseDTO dto = new ApprovalActionResponseDTO();
        dto.setName(entity.getName());
        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setAction(entity.getAction());
        if (entity.getCreatedBy() != null) {
            dto.setCreatedById(entity.getCreatedBy().getId());
            dto.setCreatedByName(entity.getCreatedBy().getName());
        }

        dto.setCreatedAt(DateFormatterUtil.format(entity.getCreatedAt()));

        if (entity.getApprovalLevel() != null) {
            dto.setApprovalLevelId(entity.getApprovalLevel().getId());
            dto.setApprovalLevelName(entity.getApprovalLevel().getName());

            // Optional: get sysApprovalId if needed
            if (entity.getApprovalLevel().getUserApproval() != null
                    && entity.getApprovalLevel().getUserApproval().getSysApproval() != null) {
                dto.setSysApprovalId(
                        entity.getApprovalLevel().getUserApproval().getSysApproval().getId()
                );
            }
        }


        dto.setApprovalLevelName(entity.getApprovalLevel() != null ? entity.getApprovalLevel().getName() : null);
        return dto;
    }
}
