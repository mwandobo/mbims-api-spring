package com.mwalimubank.mbimsapi.features.approval.repository;

import com.mwalimubank.mbimsapi.features.approval.entity.ApprovalLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ApprovalLevelRepository extends JpaRepository<ApprovalLevelEntity, Long>, JpaSpecificationExecutor<ApprovalLevelEntity> {
    ApprovalLevelEntity findByName(String name);

    Optional<ApprovalLevelEntity> findByRoleIdAndUserApprovalId(Long roleId, Long userApprovalId);

    List<ApprovalLevelEntity> findByUserApprovalIdOrderByLevelAsc(Long userApprovalId);

    Optional<ApprovalLevelEntity> findByUserApprovalIdAndLevel(Long userApprovalId, Integer level);

    List<ApprovalLevelEntity> findByUserApprovalIdAndLevelLessThanEqual(Long id, Integer level);

    Optional<ApprovalLevelEntity> findFirstByUserApprovalIdAndLevelGreaterThanOrderByLevelAsc(
            Long userApprovalId,
            Integer level
    );
    List<ApprovalLevelEntity> findByUserApprovalId(Long userApprovalId);

}