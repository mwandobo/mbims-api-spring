package com.mwalimubank.mbimsapi.features.asset_management.requested_item;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.asset_management.requested_item.dto.CreateRequestedItemDTO;
import com.mwalimubank.mbimsapi.features.asset_management.requested_item.dto.RequestedItemResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/requested-items")
@RequiredArgsConstructor
public class RequestedItemController {

    private final RequestedItemService service;

    /**
     * Get all requested items (optionally filtered by requestId)
     */
    @GetMapping
    public PagedResponse<RequestedItemResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long requestId
    ) {
        return service.findAll(pagination, search, requestId);
    }

    /**
     * Create a new requested item under a specific AssetRequest
     */
    @PostMapping("/request/{requestId}")
    public RequestedItemResponseDTO create(
            @PathVariable Long requestId,
            @RequestBody CreateRequestedItemDTO request
    ) {
        return service.create(requestId, request);
    }

    /**
     * Get single requested item by ID
     */
    @GetMapping("/{id}")
    public ApprovalAwareDTO<RequestedItemResponseDTO> findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    /**
     * Update a requested item
     */
    @PatchMapping("/{id}")
    public RequestedItemResponseDTO update(
            @PathVariable Long id,
            @RequestBody CreateRequestedItemDTO request
    ) {
        return service.update(id, request);
    }

    /**
     * Soft or hard delete
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable Long id,
            @RequestParam(name = "soft", defaultValue = "true") boolean soft
    ) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }
}