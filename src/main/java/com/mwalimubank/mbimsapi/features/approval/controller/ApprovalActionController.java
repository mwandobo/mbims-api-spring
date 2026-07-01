package com.mwalimubank.mbimsapi.features.approval.controller;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.core.dto.PaginationResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalActionRequestDTO;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalActionResponseDTO;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalLevelRequestDTO;
import com.mwalimubank.mbimsapi.features.approval.entity.ApprovalAction;
import com.mwalimubank.mbimsapi.features.approval.entity.ApprovalLevel;
import com.mwalimubank.mbimsapi.features.approval.services.ApprovalActionService;
import com.mwalimubank.mbimsapi.features.approval.services.ApprovalLevelService;
import com.mwalimubank.mbimsapi.features.user.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/approval-actions")
@RequiredArgsConstructor
public class ApprovalActionController {

    private final ApprovalActionService service;


    @GetMapping
    public PagedResponse<ApprovalActionResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search
    ) {
        return service.findAll(pagination, search);
    }

    @PostMapping
    public ApiResponse<ApprovalAction> create(@RequestBody ApprovalActionRequestDTO request) {
        return ApiResponse.success(service.create(request));
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<ApprovalActionResponseDTO> findOne(
            @PathVariable Long id
    ) {
        return service.findOne(id);
    }

    @PutMapping("/{id}")
    public ApiResponse<ApprovalAction> update(
            @PathVariable Long id,
            @RequestBody ApprovalActionRequestDTO request
    ) {
        return ApiResponse.success(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable Long id,
            @RequestParam(name = "soft", defaultValue = "false") boolean soft
    ) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }
}
