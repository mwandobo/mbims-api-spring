package com.mwalimubank.mbimsapi.features.approval.repository;

import com.mwalimubank.mbimsapi.features.approval.entity.ApprovalAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalActionRepository extends JpaRepository<ApprovalAction, Long> {
    ApprovalAction findByName(String name);

    Page<ApprovalAction> findAll(Specification<ApprovalAction> spec, Pageable pageable);
    List<ApprovalAction> findByApprovalLevelId(Long approvalLevelId);
    Optional<ApprovalAction> findByApprovalLevelIdAndEntityId(Long approvalLevelId, Long entityId);
    List<ApprovalAction> findByEntityIdAndApprovalLevelIdIn(Long entityId, List<Long> levelIds);
    List<ApprovalAction> findByEntityNameAndEntityIdIn(String entityName, List<Long> entityIds);
}