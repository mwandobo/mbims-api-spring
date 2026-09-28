package com.mwalimubank.mbimsapi.features.asset_management.asset_request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface AssetRequestRepository extends JpaRepository<AssetRequestEntity, Long> , JpaSpecificationExecutor<AssetRequestEntity>{
    Optional<AssetRequestEntity> findByName(String name);
}
