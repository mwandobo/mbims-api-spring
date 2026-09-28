package com.mwalimubank.mbimsapi.features.asset_management.asset_request;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.features.asset_management.asset_request.dto.AssetRequestResponseDTO;
import com.mwalimubank.mbimsapi.features.asset_management.asset_request.dto.CreateAssetRequestDTO;
import com.mwalimubank.mbimsapi.features.asset_management.requested_item.RequestedItemEntity;
import com.mwalimubank.mbimsapi.features.asset_management.requested_item.RequestedItemRepository;
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
public class AssetRequestService {

    private final AssetRequestRepository repository;
    private final RequestedItemRepository requestedItemRepository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;
    private final PagedQueryService pagedQueryService;

    private static final Set<String> SORT_FIELDS = Set.of(
            "id", "name", "status", "createdAt"
    );

    private static final Map<String, String> SORT_ALIASES = Map.of(
            // add aliases if needed
    );

    // -------------------- LIST --------------------
    public PagedResponse<AssetRequestResponseDTO> findAll(
            PaginationRequest pagination,
            String search
    ) {
        Specification<AssetRequestEntity> spec = PageSpecs.and(
                PageSpecs.notDeleted(),
                PageSpecs.searchLike(search, "name", "description")
        );

        return pagedQueryService.findAll(
                repository,
                spec,
                pagination,
                AssetRequestEntity.class,
                AssetRequestEntity::getId,
                AssetRequestResponseDTO::fromEntity,
                AssetRequestResponseDTO::setApprovalStatus,
                SORT_FIELDS,
                SORT_ALIASES
        );
    }

    // -------------------- CREATE --------------------
    @Transactional
    public AssetRequestResponseDTO create(CreateAssetRequestDTO request) {
        AssetRequestEntity entity = new AssetRequestEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        // optionally set current user
        entity.setCreatedBy(currentUserService.getCurrentUser());

        AssetRequestEntity saved = repository.save(entity);
        return AssetRequestResponseDTO.fromEntity(saved);
    }

    // -------------------- FIND ONE --------------------
    public ApprovalAwareDTO<AssetRequestResponseDTO> findOne(Long id) {
        AssetRequestEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("AssetRequest not found"));

        return approvalStatusUtil.attachApprovalInfo(
                AssetRequestResponseDTO.fromEntity(entity),
                entity.getId(),
                AssetRequestEntity.class.getSimpleName(),
                currentUserService.getCurrentUserRoleId()
        );
    }

    // -------------------- UPDATE --------------------
    @Transactional
    public AssetRequestResponseDTO update(Long id, CreateAssetRequestDTO request) {
        AssetRequestEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("AssetRequest not found"));

        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }

        entity.setCreatedBy(currentUserService.getCurrentUser());

        AssetRequestEntity updated = repository.save(entity);
        return AssetRequestResponseDTO.fromEntity(updated);
    }

    // -------------------- SUBMIT --------------------
    @Transactional
    public String submit(Long id) {
        AssetRequestEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("AssetRequest not found"));

        entity.setStatus(1);
        repository.save(entity);

        return "Request \"" + entity.getName() + "\" submitted successfully";
    }

    // -------------------- DELETE --------------------
    @Transactional
    public void delete(Long id, boolean soft) {
        AssetRequestEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("AssetRequest not found"));

        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }

    // -------------------- ITEMS UNDER A REQUEST (paginated) --------------------
    public PagedResponse<RequestedItemResponseDTO> findItemsByRequestId(
            Long requestId,
            PaginationRequest pagination,
            String search
    ) {
        // Ensure parent exists
        if (!repository.existsById(requestId)) {
            throw new IllegalStateException("AssetRequest not found");
        }

        Specification<RequestedItemEntity> spec = PageSpecs.and(
                PageSpecs.notDeleted(),
                (root, query, cb) -> cb.equal(root.get("request").get("id"), requestId),
                PageSpecs.searchLike(search, "asset.name") // adjust field names as needed
        );

        return pagedQueryService.findAll(
                requestedItemRepository,
                spec,
                pagination,
                RequestedItemEntity.class,
                RequestedItemEntity::getId,
                RequestedItemResponseDTO::fromEntity,
                RequestedItemResponseDTO::setApprovalStatus,
                Set.of("id", "quantity", "createdAt"),
                Map.of()
        );
    }

    // -------------------- SINGLE ITEM UNDER A REQUEST --------------------
    public RequestedItemResponseDTO findItemByRequestAndItemId(Long requestId, Long itemId) {
        RequestedItemEntity item = requestedItemRepository
                .findByIdAndRequestId(itemId, requestId)   // you need this method in repository
                .orElseThrow(() -> new IllegalStateException(
                        "Item not found in the specified AssetRequest"));

        return RequestedItemResponseDTO.fromEntity(item);
    }
}