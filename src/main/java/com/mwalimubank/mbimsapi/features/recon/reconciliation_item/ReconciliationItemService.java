package com.mwalimubank.mbimsapi.features.recon.reconciliation_item;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.recon.reconciliation_item.dto.CreateReconciliationItemDTO;
import com.mwalimubank.mbimsapi.features.common.services.PagedQueryService;

import com.mwalimubank.mbimsapi.features.recon.reconciliation_item.dto.ReconciliationItemResponseDTO;
import com.mwalimubank.mbimsapi.features.recon.reconciliation_item.ReconciliationItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationDto;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;
import com.mwalimubank.mbimsapi.features.common.services.PagedQueryService;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.common.PageSpecs;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReconciliationItemService {
    private final ReconciliationItemRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;
    private final PagedQueryService pagedQueryService;

    private static final Set<String> SORT_FIELDS = Set.of(
            "id", "name"
    );

     private static final Map<String, String> SORT_ALIASES = Map.of(
            // frontend column id → entity field if needed eg department.name to departmentName
    );

    public PagedResponse<ReconciliationItemResponseDTO> findAll(
            PaginationRequest pagination,
            String search
    ) {
           Specification<ReconciliationItemEntity> spec = PageSpecs.and(
                  PageSpecs.notDeleted(),
                  PageSpecs.searchLike(search,
                          "name"
                  )
          );

          return pagedQueryService.findAll(
                  repository,
                  spec,
                  pagination,
                  ReconciliationItemEntity.class,
                  ReconciliationItemEntity::getId,
                  ReconciliationItemResponseDTO::fromEntity,
                  ReconciliationItemResponseDTO::setApprovalStatus,
                  SORT_FIELDS,
                  SORT_ALIASES
          );
    }

    @Transactional
    public ReconciliationItemResponseDTO create(CreateReconciliationItemDTO request) {
        ReconciliationItemEntity entity = new ReconciliationItemEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        ReconciliationItemEntity saved = repository.save(entity);
        return ReconciliationItemResponseDTO.fromEntity(saved);
    }

    public ApprovalAwareDTO<ReconciliationItemResponseDTO> findOne(Long id) {
        ReconciliationItemEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("ReconciliationItem not found"));
        return approvalStatusUtil.attachApprovalInfo(
                ReconciliationItemResponseDTO.fromEntity(entity),
                entity.getId(),
                ReconciliationItemEntity.class.getSimpleName(),
                currentUserService.getCurrentUserRoleId()
        );
    }

    @Transactional
    public ReconciliationItemResponseDTO update(Long id, CreateReconciliationItemDTO request) {
        ReconciliationItemEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("ReconciliationItem not found"));

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());

        ReconciliationItemEntity updated = repository.save(entity);
        return ReconciliationItemResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        ReconciliationItemEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("ReconciliationItem not found"));
        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}
