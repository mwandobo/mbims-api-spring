package com.mwalimubank.mbimsapi.features.recon.reconciliation_item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ReconciliationItemRepository extends JpaRepository<ReconciliationItemEntity, Long> , JpaSpecificationExecutor<ReconciliationItemEntity>{
    Optional<ReconciliationItemEntity> findByName(String name);

    // in ReconciliationItemRepository
    List<ReconciliationItemEntity> findByReconciliationId(Long reconciliationId);
}
