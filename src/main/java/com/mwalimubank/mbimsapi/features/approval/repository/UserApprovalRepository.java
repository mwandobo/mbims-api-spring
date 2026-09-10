package com.mwalimubank.mbimsapi.features.approval.repository;

import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.approval.entity.UserApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserApprovalRepository extends JpaRepository<UserApproval, Long>, JpaSpecificationExecutor<UserApproval> {
    UserApproval findByName(String name);

    Optional<UserApproval> findBySysApprovalId(Long sysApprovalId);
}