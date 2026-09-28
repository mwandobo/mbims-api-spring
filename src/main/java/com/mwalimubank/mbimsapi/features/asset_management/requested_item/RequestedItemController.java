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

    @GetMapping
    public PagedResponse<RequestedItemResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long requestId
    ) {
        return service.findAll(pagination, search, requestId);
    }

    @PostMapping
    public RequestedItemResponseDTO create(
            @RequestParam Long requestId,
            @RequestBody CreateRequestedItemDTO request
    ) {
        return service.create(requestId, request);
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<RequestedItemResponseDTO> findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public RequestedItemResponseDTO update(
            @PathVariable Long id,
            @RequestBody CreateRequestedItemDTO request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable Long id,
            @RequestParam(name = "soft", defaultValue = "true") boolean soft
    ) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }

}