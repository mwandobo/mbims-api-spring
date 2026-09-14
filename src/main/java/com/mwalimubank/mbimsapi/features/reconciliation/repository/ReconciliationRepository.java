package com.mwalimubank.mbimsapi.features.reconciliation.repository;

import com.mwalimubank.mbimsapi.features.reconciliation.entity.ReconciliationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReconciliationRepository extends JpaRepository<ReconciliationEntity, Long>, JpaSpecificationExecutor<ReconciliationEntity> {
}
