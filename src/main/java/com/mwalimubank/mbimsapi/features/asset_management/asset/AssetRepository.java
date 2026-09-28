package com.mwalimubank.mbimsapi.features.asset_management.asset;

import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<AssetEntity, Long>, JpaSpecificationExecutor<AssetEntity> {
    Optional<AssetEntity> findByName(String name);



    @Query("""
    SELECT a FROM AssetEntity a
    WHERE a.assetCategory.id = :categoryId
      AND a.deleted = false
    """)
    List<AssetEntity> findByAssetCategoryId(@Param("categoryId") Long categoryId);

}
