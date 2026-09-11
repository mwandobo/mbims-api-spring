package com.mwalimubank.mbimsapi.features.asset_management.asset;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.administration.department.dto.DepartmentResponseDTO;
import com.mwalimubank.mbimsapi.features.asset_management.asset.dto.CreateAssetDTO;
import com.mwalimubank.mbimsapi.features.asset_management.asset.dto.AssetResponseDTO;
import com.mwalimubank.mbimsapi.features.asset_management.assetcategory.AssetCategoryEntity;
import com.mwalimubank.mbimsapi.features.asset_management.assetcategory.AssetCategoryRepository;
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
public class AssetService {
    private final AssetRepository repository;
    private final AssetCategoryRepository assetcategoryRepository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;
    private final PagedQueryService pagedQueryService;

    private static final Set<String> SORT_FIELDS = Set.of(
            "id", "name", "description","assetCategory.name"
    );

    private static final Map<String, String> SORT_ALIASES = Map.of(
            "assetCategoryName", "assetCategory.name"   // frontend sortBy=departmentName
    );



    public PagedResponse<AssetResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<AssetEntity> spec = PageSpecs.and(
                PageSpecs.notDeleted(),
                PageSpecs.searchLike(search, "name", "description","assetCategory.name")
        );

        return pagedQueryService.findAll(
                repository,
                spec,
                pagination,
                AssetEntity.class,
                AssetEntity::getId,
                AssetResponseDTO::fromEntity,
                AssetResponseDTO::setApprovalStatus,
                SORT_FIELDS,
                SORT_ALIASES
        );
    }

    @Transactional
    public AssetResponseDTO create(CreateAssetDTO request) {
        repository.findByName(request.getName())
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Asset with name '" + request.getName() + "' already exists"
                    );
                });

        AssetEntity entity = new AssetEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        AssetCategoryEntity assetcategory = validateAssetcategoryExists(request.getAsset_category_id());
        entity.setAssetCategory(assetcategory);
        AssetEntity savedEntity = repository.save(entity);

        return  AssetResponseDTO.fromEntity(savedEntity);
    }

    public  ApprovalAwareDTO<AssetResponseDTO> findOne  (Long  assetId) {
          AssetEntity   asset = repository.findById( assetId)
                 .orElseThrow(() -> new IllegalStateException(" Asset not found"));

          AssetResponseDTO dto = AssetResponseDTO.fromEntity(asset);

           return approvalStatusUtil.attachApprovalInfo(
                    dto,
                    asset.getId(),
                    AssetEntity.class.getSimpleName(),
                    currentUserService.getCurrentUserRoleId()
                );
     }

    @Transactional
    public AssetResponseDTO update(Long id, CreateAssetDTO request) {
        AssetEntity entity = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Asset not found with id: " + id
                        )
                );

        repository.findByName(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Asset with name '" + request.getName() + "' already exists"
                    );
                });

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        AssetCategoryEntity assetcategory = validateAssetcategoryExists(request.getAsset_category_id());
        entity.setAssetCategory(assetcategory);

        AssetEntity updatedEntity = repository.save(entity);

        return  AssetResponseDTO.fromEntity(updatedEntity);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        AssetEntity entity = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Asset not found with id: " + id
                        )
                );

        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }

    private AssetCategoryEntity validateAssetcategoryExists(Long id) {
        if (id == null) {
            if ("false" == "false") {
                throw new IllegalArgumentException("Assetcategory ID is required");
            }
            return null;
        }
        return assetcategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Assetcategory not found with id: " + id));
    }

}
