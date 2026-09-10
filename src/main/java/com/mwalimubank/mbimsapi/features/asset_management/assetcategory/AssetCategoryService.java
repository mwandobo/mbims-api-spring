package com.mwalimubank.mbimsapi.features.asset_management.assetcategory;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.administration.department.dto.DepartmentResponseDTO;
import com.mwalimubank.mbimsapi.features.asset_management.assetcategory.dto.CreateAssetCategoryDTO;
import com.mwalimubank.mbimsapi.features.asset_management.assetcategory.dto.AssetCategoryResponseDTO;
import com.mwalimubank.mbimsapi.features.common.PageSpecs;
import com.mwalimubank.mbimsapi.features.common.services.PagedQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationDto;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AssetCategoryService {
    private final AssetCategoryRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;
    private final PagedQueryService pagedQueryService;

    private static final Set<String> DEPARTMENT_SORT_FIELDS = Set.of(
            "id", "name", "description"
    );

    public PagedResponse<AssetCategoryResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<AssetCategoryEntity> spec = PageSpecs.and(
                PageSpecs.notDeleted(),
                PageSpecs.searchLike(search, "name", "description")
        );

        return pagedQueryService.findAll(
                repository,
                spec,
                pagination,
                AssetCategoryEntity.class,
                AssetCategoryEntity::getId,
                AssetCategoryResponseDTO::fromEntity,
                AssetCategoryResponseDTO::setApprovalStatus,
                DEPARTMENT_SORT_FIELDS
        );
    }

    @Transactional
    public AssetCategoryResponseDTO create(CreateAssetCategoryDTO request) {
        repository.findByName(request.getName())
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "AssetCategory with name '" + request.getName() + "' already exists"
                    );
                });

        AssetCategoryEntity entity = new AssetCategoryEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        AssetCategoryEntity savedEntity = repository.save(entity);

        return  AssetCategoryResponseDTO.fromEntity(savedEntity);
    }

    public  ApprovalAwareDTO<AssetCategoryResponseDTO> findOne  (Long  assetcategoryId) {
          AssetCategoryEntity   assetcategory = repository.findById( assetcategoryId)
                 .orElseThrow(() -> new IllegalStateException(" AssetCategory not found"));

          AssetCategoryResponseDTO dto = AssetCategoryResponseDTO.fromEntity(assetcategory);

           return approvalStatusUtil.attachApprovalInfo(
                    dto,
                    assetcategory.getId(),
                    AssetCategoryEntity.class.getSimpleName(),
                    currentUserService.getCurrentUserRoleId()
                );
     }

    @Transactional
    public AssetCategoryResponseDTO update(Long id, CreateAssetCategoryDTO request) {
        AssetCategoryEntity entity = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "AssetCategory not found with id: " + id
                        )
                );

        repository.findByName(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "AssetCategory with name '" + request.getName() + "' already exists"
                    );
                });

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());

        AssetCategoryEntity updatedEntity = repository.save(entity);

        return  AssetCategoryResponseDTO.fromEntity(updatedEntity);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        AssetCategoryEntity entity = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "AssetCategory not found with id: " + id
                        )
                );

        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}
