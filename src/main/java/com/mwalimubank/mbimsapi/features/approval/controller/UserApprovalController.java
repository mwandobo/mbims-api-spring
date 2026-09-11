package com.mwalimubank.mbimsapi.features.approval.controller;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.approval.dto.UserApprovalRequestDTO;
import com.mwalimubank.mbimsapi.features.approval.dto.UserApprovalResponseDTO;
import com.mwalimubank.mbimsapi.features.approval.entity.UserApproval;
import com.mwalimubank.mbimsapi.features.approval.services.UserApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-approvals")
@RequiredArgsConstructor
public class UserApprovalController {

    private final UserApprovalService service;

    // ✅ Get all UserApprovals (with search + pagination)
    @GetMapping
    public PagedResponse<UserApprovalResponseDTO> getAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search
    ) {
        return  service.findAll(pagination, search);
    }

    // ✅ Create new UserApproval
    @PostMapping
    public ApiResponse<UserApproval> create(@RequestBody UserApprovalRequestDTO request) {
        return ApiResponse.success(service.create(request));
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<UserApprovalResponseDTO> findOne(
            @PathVariable Long id
    ) {
        return service.findOne(id);
    }

    // ✅ Update UserApproval
    @PutMapping("/{id}")
    public ApiResponse<UserApproval> update(
            @PathVariable Long id,
            @RequestBody UserApprovalRequestDTO request
    ) {
        return ApiResponse.success(service.update(id, request));
    }

    // ✅ Delete UserApproval (soft or hard)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable Long id,
            @RequestParam(name = "soft", defaultValue = "false") boolean soft
    ) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }
}
