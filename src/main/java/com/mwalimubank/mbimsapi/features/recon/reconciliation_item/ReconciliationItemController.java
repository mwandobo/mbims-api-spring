package com.mwalimubank.mbimsapi.features.recon.reconciliation_item;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.recon.reconciliation_item.dto.CreateReconciliationItemDTO;
import com.mwalimubank.mbimsapi.features.recon.reconciliation_item.dto.ReconciliationItemResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reconciliation-items")
@RequiredArgsConstructor
public class ReconciliationItemController {

    private final ReconciliationItemService service;

    @GetMapping
    public PagedResponse<ReconciliationItemResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search) {
        return service.findAll(pagination, search);
    }

    @PostMapping
    public ReconciliationItemResponseDTO create(@RequestBody CreateReconciliationItemDTO request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<ReconciliationItemResponseDTO> findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public ReconciliationItemResponseDTO update(@PathVariable Long id, @RequestBody CreateReconciliationItemDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestParam(name = "soft", defaultValue = "false") boolean soft) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }
}
