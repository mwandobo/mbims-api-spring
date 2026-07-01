package com.mwalimubank.mbimsapi.features.approval.dto;

import com.mwalimubank.mbimsapi.features.approval.entity.SysApproval;
import com.mwalimubank.mbimsapi.features.approval.entity.UserApproval;
import com.mwalimubank.mbimsapi.features.approval.enums.StatusEnum;
import lombok.Data;

@Data
public class SysApprovalResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String approvalStatus;
    private String entityName;
    private StatusEnum status;

    public static SysApprovalResponseDTO fromEntity(SysApproval entity) {
        SysApprovalResponseDTO dto = new SysApprovalResponseDTO();
        dto.setName(entity.getName());
        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
