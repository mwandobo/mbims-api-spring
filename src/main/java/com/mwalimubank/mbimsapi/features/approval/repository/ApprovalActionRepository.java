package com.mwalimubank.mbimsapi.features.approval.repository;

import com.mwalimubank.mbimsapi.features.approval.entity.ApprovalActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ApprovalActionRepository extends JpaRepository<ApprovalActionEntity, Long>, JpaSpecificationExecutor<ApprovalActionEntity> {
    ApprovalActionEntity findByName(String name);

    List<ApprovalActionEntity> findByApprovalLevelId(Long approvalLevelId);
    Optional<ApprovalActionEntity> findByApprovalLevelIdAndEntityId(Long approvalLevelId, Long entityId);
    List<ApprovalActionEntity> findByEntityIdAndApprovalLevelIdIn(Long entityId, List<Long> levelIds);
    List<ApprovalActionEntity> findByEntityNameAndEntityIdIn(String entityName, List<Long> entityIds);
}