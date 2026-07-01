package com.mwalimubank.mbimsapi.features.approval.controller;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.core.dto.PaginationResponse;
import com.mwalimubank.mbimsapi.features.approval.entity.SysApproval;
import com.mwalimubank.mbimsapi.features.approval.services.SysApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sys-approvals")
@RequiredArgsConstructor
public class SysApprovalController {

    private final SysApprovalService service;

    @GetMapping()
    public ApiResponse<PaginationResponse<SysApproval>> getAllPermissions(
            PaginationRequest pagination,
            @RequestParam(required = false) String search
    ) {
        return ApiResponse.success(
                service.findAll(pagination, search)
        );
    }
}
