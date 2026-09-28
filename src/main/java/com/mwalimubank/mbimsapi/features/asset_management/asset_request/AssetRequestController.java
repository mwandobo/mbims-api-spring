package com.mwalimubank.mbimsapi.features.asset_management.asset_request;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.asset_management.asset_request.dto.CreateAssetRequestDTO;
import com.mwalimubank.mbimsapi.features.asset_management.asset_request.dto.AssetRequestResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/asset-requests")
@RequiredArgsConstructor
public class AssetRequestController {

    private final AssetRequestService service;

    @GetMapping
    public PagedResponse<AssetRequestResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search) {
        return service.findAll(pagination, search);
    }

    @PostMapping
    public AssetRequestResponseDTO create(@RequestBody CreateAssetRequestDTO request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<AssetRequestResponseDTO> findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public AssetRequestResponseDTO update(@PathVariable Long id, @RequestBody CreateAssetRequestDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestParam(name = "soft", defaultValue = "false") boolean soft) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }
}
