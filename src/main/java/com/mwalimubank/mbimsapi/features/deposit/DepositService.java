package com.mwalimubank.mbimsapi.features.deposit;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.deposit.dto.CreateDepositDTO;
import com.mwalimubank.mbimsapi.features.common.services.PagedQueryService;

import com.mwalimubank.mbimsapi.features.deposit.dto.DepositResponseDTO;
import com.mwalimubank.mbimsapi.features.deposit.DepositEntity;
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
public class DepositService {
    private final DepositRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;
    private final PagedQueryService pagedQueryService;

    private static final Set<String> SORT_FIELDS = Set.of(
            "id", ""
    );

     private static final Map<String, String> SORT_ALIASES = Map.of(
            // frontend column id → entity field if needed eg department.name to departmentName
    );

    public PagedResponse<DepositResponseDTO> findAll(
            PaginationRequest pagination,
            String search
    ) {
           Specification<DepositEntity> spec = PageSpecs.and(
                  PageSpecs.notDeleted(),
                  PageSpecs.searchLike(search
                  )
          );

          return pagedQueryService.findAll(
                  repository,
                  spec,
                  pagination,
                  DepositEntity.class,
                  DepositEntity::getId,
                  DepositResponseDTO::fromEntity,
                  DepositResponseDTO::setApprovalStatus,
                  SORT_FIELDS,
                  SORT_ALIASES
          );
    }

    @Transactional
    public DepositResponseDTO create(CreateDepositDTO request) {
        DepositEntity entity = new DepositEntity();

        DepositEntity saved = repository.save(entity);
        return DepositResponseDTO.fromEntity(saved);
    }

    public ApprovalAwareDTO<DepositResponseDTO> findOne(Long id) {
        DepositEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Deposit not found"));
        return approvalStatusUtil.attachApprovalInfo(
                DepositResponseDTO.fromEntity(entity),
                entity
        );
    }

    @Transactional
    public DepositResponseDTO update(Long id, CreateDepositDTO request) {
        DepositEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Deposit not found"));



        DepositEntity updated = repository.save(entity);
        return DepositResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        DepositEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Deposit not found"));
        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}
