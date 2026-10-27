package com.mwalimubank.mbimsapi.features.approval.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateApprovalOverrideRequest {
    private String entityName;                     // e.g. "AssetRequest"
    private List<ApprovalLevelRequestDTO> levels;  // the list coming from the frontend
}