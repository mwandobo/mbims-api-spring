package com.mwalimubank.mbimsapi.features.approval.controller;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalLevelRequestDTO;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalLevelResponseDTO;
import com.mwalimubank.mbimsapi.features.approval.entity.ApprovalLevel;
import com.mwalimubank.mbimsapi.features.approval.services.ApprovalLevelService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/approval-levels")
@RequiredArgsConstructor
public class ApprovalLevelController {

    private final ApprovalLevelService service;

    @GetMapping
    public PagedResponse<ApprovalLevelResponseDTO> getAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search
    ) {
        return service.findAll(pagination, search);
    }

    @PostMapping
    public ApprovalLevelResponseDTO create(
                    @RequestParam(required = false) Long userApprovalId,
        @RequestBody ApprovalLevelRequestDTO request) throws MessagingException {
        return service.create(userApprovalId,request);
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<ApprovalLevelResponseDTO> findOne(
            @PathVariable Long id
    ) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public ApiResponse<ApprovalLevel> update(
                            @RequestParam(required = false) Long userApprovalId,
            @PathVariable Long id,
            @RequestBody ApprovalLevelRequestDTO request
    ) {
        return ApiResponse.success(service.update(id,userApprovalId, request));
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
