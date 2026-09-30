package com.mwalimubank.mbimsapi.features.approval.repository;

import com.mwalimubank.mbimsapi.features.approval.entity.ApprovalActionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalActionRepository extends JpaRepository<ApprovalActionEntity, Long> {
    ApprovalActionEntity findByName(String name);

    Page<ApprovalActionEntity> findAll(Specification<ApprovalActionEntity> spec, Pageable pageable);
    List<ApprovalActionEntity> findByApprovalLevelId(Long approvalLevelId);
    Optional<ApprovalActionEntity> findByApprovalLevelIdAndEntityId(Long approvalLevelId, Long entityId);
    List<ApprovalActionEntity> findByEntityIdAndApprovalLevelIdIn(Long entityId, List<Long> levelIds);
    List<ApprovalActionEntity> findByEntityNameAndEntityIdIn(String entityName, List<Long> entityIds);
}