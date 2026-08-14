package com.mwalimubank.mbimsapi.features.common.generic_detail;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.common.generic_detail.dto.CreateGenericDetailDTO;
import com.mwalimubank.mbimsapi.features.common.generic_detail.dto.GenericDetailResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.common.generic_detail.service.GenericDetailService;
import com.mwalimubank.mbimsapi.features.common.generic_detail.service.GenericParameterSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/generic-details")
@RequiredArgsConstructor
public class GenericDetailController {

    private final GenericDetailService service;
    private final GenericParameterSyncService genericParameterSyncService;

    @GetMapping
    public PagedResponse<GenericDetailResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search) {
        return service.findAll(pagination, search);
    }

    @PostMapping
    public GenericDetailResponseDTO create(@RequestBody CreateGenericDetailDTO request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<GenericDetailResponseDTO> findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public GenericDetailResponseDTO update(@PathVariable Long id, @RequestBody CreateGenericDetailDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestParam(name = "soft", defaultValue = "false") boolean soft) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncGenericParameter() {
        genericParameterSyncService.syncGenericParametersAsync();   // fire and forget
        return ResponseEntity.accepted().body(Map.of(
                "message", "Generic Parameter sync started in background"
        ));
    }
}
