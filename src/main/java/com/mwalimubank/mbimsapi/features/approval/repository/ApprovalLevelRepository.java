package com.mwalimubank.mbimsapi.features.approval.repository;

import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.approval.entity.ApprovalLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ApprovalLevelRepository extends JpaRepository<ApprovalLevel, Long>, JpaSpecificationExecutor<ApprovalLevel> {
    ApprovalLevel findByName(String name);

    Optional<ApprovalLevel> findByRoleIdAndUserApprovalId(Long roleId, Long userApprovalId);

    List<ApprovalLevel> findByUserApprovalIdOrderByLevelAsc(Long userApprovalId);

    Optional<ApprovalLevel> findByUserApprovalIdAndLevel(Long userApprovalId, Integer level);

    List<ApprovalLevel> findByUserApprovalIdAndLevelLessThanEqual(Long id, Integer level);

    Optional<ApprovalLevel> findFirstByUserApprovalIdAndLevelGreaterThanOrderByLevelAsc(
            Long userApprovalId,
            Integer level
    );
    List<ApprovalLevel> findByUserApprovalId(Long userApprovalId);

}