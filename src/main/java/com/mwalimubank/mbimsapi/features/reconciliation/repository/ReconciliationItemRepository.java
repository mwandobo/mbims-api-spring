package com.mwalimubank.mbimsapi.features.reconciliation.repository;

import com.mwalimubank.mbimsapi.features.reconciliation.entity.ReconciliationItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReconciliationItemRepository extends JpaRepository<ReconciliationItemEntity, Long>, JpaSpecificationExecutor<ReconciliationItemEntity> {
}
