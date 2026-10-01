package com.mwalimubank.mbimsapi.features.asset_management.requested_item;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.features.asset_management.asset.AssetEntity;
import com.mwalimubank.mbimsapi.features.asset_management.asset.AssetRepository;
import com.mwalimubank.mbimsapi.features.asset_management.asset_request.AssetRequestEntity;
import com.mwalimubank.mbimsapi.features.asset_management.asset_request.AssetRequestRepository;
import com.mwalimubank.mbimsapi.features.asset_management.requested_item.dto.CreateRequestedItemDTO;
import com.mwalimubank.mbimsapi.features.asset_management.requested_item.dto.RequestedItemResponseDTO;
import com.mwalimubank.mbimsapi.features.common.PageSpecs;
import com.mwalimubank.mbimsapi.features.common.services.PagedQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RequestedItemService {

    private final RequestedItemRepository repository;
    private final AssetRequestRepository assetRequestRepository;
    private final AssetRepository assetRepository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;
    private final PagedQueryService pagedQueryService;

    private static final Set<String> SORT_FIELDS = Set.of(
            "id", "quantity", "createdAt"
    );

    private static final Map<String, String> SORT_ALIASES = Map.of(
            // e.g. "assetName" → "asset.name"
    );

    // -------------------- LIST (optionally filtered by requestId) --------------------
    public PagedResponse<RequestedItemResponseDTO> findAll(
            PaginationRequest pagination,
            String search,
            Long requestId          // optional filter
    ) {
        Specification<RequestedItemEntity> spec = PageSpecs.and(
                PageSpecs.notDeleted(),
                requestId != null
                        ? (root, query, cb) -> cb.equal(root.get("request").get("id"), requestId)
                        : null,
                PageSpecs.searchLike(search, "asset.name")
        );

        return pagedQueryService.findAll(
                repository,
                spec,
                pagination,
                RequestedItemEntity.class,
                RequestedItemEntity::getId,
                RequestedItemResponseDTO::fromEntity,
                RequestedItemResponseDTO::setApprovalStatus,
                SORT_FIELDS,
                SORT_ALIASES
        );
    }

    // -------------------- CREATE (linked to a request + asset) --------------------
    @Transactional
    public RequestedItemResponseDTO create(Long requestId, CreateRequestedItemDTO dto) {
        AssetRequestEntity request = assetRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalStateException("AssetRequest not found"));

        AssetEntity asset = assetRepository.findById(dto.getAssetId())
                .orElseThrow(() -> new IllegalStateException("Asset not found"));

        RequestedItemEntity entity = new RequestedItemEntity();
        entity.setRequest(request);
        entity.setAsset(asset);
        entity.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 1);

        RequestedItemEntity saved = repository.save(entity);
        return RequestedItemResponseDTO.fromEntity(saved);
    }

    // -------------------- FIND ONE --------------------
    public ApprovalAwareDTO<RequestedItemResponseDTO> findOne(Long id) {
        RequestedItemEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("RequestedItem not found"));

        return approvalStatusUtil.attachApprovalInfo(
                RequestedItemResponseDTO.fromEntity(entity),
                entity
        );
    }

    // -------------------- UPDATE --------------------
    @Transactional
    public RequestedItemResponseDTO update(Long id, CreateRequestedItemDTO dto) {
        RequestedItemEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("RequestedItem not found"));

        if (dto.getAssetId() != null) {
            AssetEntity asset = assetRepository.findById(dto.getAssetId())
                    .orElseThrow(() -> new IllegalStateException("Asset not found"));
            entity.setAsset(asset);
        }

        if (dto.getQuantity() != null) {
            entity.setQuantity(dto.getQuantity());
        }

        RequestedItemEntity updated = repository.save(entity);
        return RequestedItemResponseDTO.fromEntity(updated);
    }

    // -------------------- DELETE --------------------
    @Transactional
    public void delete(Long id, boolean soft) {
        RequestedItemEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("RequestedItem not found"));

        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}