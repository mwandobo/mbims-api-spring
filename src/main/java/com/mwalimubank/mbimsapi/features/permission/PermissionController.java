package com.mwalimubank.mbimsapi.features.permission;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.core.dto.PaginationResponse;
import com.mwalimubank.mbimsapi.features.permission.dto.AssignPermissionRequest;
import com.mwalimubank.mbimsapi.features.permission.services.PermissionService;
import com.mwalimubank.mbimsapi.features.role.RoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService service;

    @GetMapping()
    public ApiResponse<PaginationResponse<PermissionEntity>> getAllPermissions(
            PaginationRequest pagination,
            @RequestParam(required = false) String search
    ) {
        return ApiResponse.success(
                service.findAll(pagination, search)
        );
    }

    @PostMapping("/assign")
    public RoleEntity assignPermissions(@RequestBody AssignPermissionRequest request) {
        return service.assignPermissionsToRole(request);
    }
}
