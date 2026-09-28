package com.mwalimubank.mbimsapi.features.asset_management.requested_item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface RequestedItemRepository extends JpaRepository<RequestedItemEntity, Long> , JpaSpecificationExecutor<RequestedItemEntity>{
    Optional<RequestedItemEntity> findByName(String name);
    Optional<RequestedItemEntity> findByIdAndRequestId(Long id, Long requestId);
}
