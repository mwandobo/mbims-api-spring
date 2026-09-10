package com.mwalimubank.mbimsapi.features.asset_management.assetcategory;

import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AssetCategoryRepository extends JpaRepository<AssetCategoryEntity, Long>, JpaSpecificationExecutor<AssetCategoryEntity> {
    Optional<AssetCategoryEntity> findByName(String name);
}
