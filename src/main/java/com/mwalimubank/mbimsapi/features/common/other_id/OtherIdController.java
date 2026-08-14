package com.mwalimubank.mbimsapi.features.common.other_id;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.common.other_id.dto.CreateOtherIdDTO;
import com.mwalimubank.mbimsapi.features.common.other_id.dto.OtherIdResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.common.other_id.service.OtherIdService;
import com.mwalimubank.mbimsapi.features.customer.services.OtherIdSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/other-ids")
@RequiredArgsConstructor
public class OtherIdController {

    private final OtherIdService service;
    private final OtherIdSyncService otherIdSyncService;

    @GetMapping
    public PagedResponse<OtherIdResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search) {
        return service.findAll(pagination, search);
    }

    @PostMapping
    public OtherIdResponseDTO create(@RequestBody CreateOtherIdDTO request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<OtherIdResponseDTO> findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public OtherIdResponseDTO update(@PathVariable Long id, @RequestBody CreateOtherIdDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestParam(name = "soft", defaultValue = "false") boolean soft) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncOtherId() {
        otherIdSyncService.syncOtherIdsAsync();   // fire and forget
        return ResponseEntity.accepted().body(Map.of(
                "message", "Other Id sync started in background"
        ));
    }
}
