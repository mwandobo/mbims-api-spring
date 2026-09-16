package com.mwalimubank.mbimsapi.features.reconciliation.repository;

import com.mwalimubank.mbimsapi.features.reconciliation.entity.ReconciliationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReconciliationRepository extends JpaRepository<ReconciliationEntity, Long>, JpaSpecificationExecutor<ReconciliationEntity> {

    Optional<ReconciliationEntity> findTopByOrderByIdDesc();

    // or, more accurate if code is the source of truth:
    @Query("SELECT r.code FROM ReconciliationEntity r WHERE r.code LIKE 'RECON%' ORDER BY r.id DESC LIMIT 1")
    Optional<String> findLastCode();

    Optional<ReconciliationEntity> findFirstByCodeStartingWithOrderByIdDesc(String prefix);
}
