package com.mwalimubank.mbimsapi.features.administration.department;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.core.dto.PaginationResponse;
import com.mwalimubank.mbimsapi.features.administration.department.dto.CreateDepartmentDTO;
import com.mwalimubank.mbimsapi.features.administration.department.dto.DepartmentResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService service;

    @GetMapping
    public PagedResponse<DepartmentResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search
    ) {
                return service.findAll(pagination, search);
    }

    @PostMapping
    public DepartmentResponseDTO  create(
            @RequestBody CreateDepartmentDTO request
    ) {
        return service.create(request);
    }

     @GetMapping("/{id}")
        public ApprovalAwareDTO<DepartmentResponseDTO> findOne(
                @PathVariable Long id
        ) {
            return service.findOne(id);
        }

    @PatchMapping("/{id}")
    public DepartmentResponseDTO update(
            @PathVariable Long id,
            @RequestBody CreateDepartmentDTO request
    ) {
        return service.update(id, request);
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
