package com.mwalimubank.mbimsapi.features.approval.repository;

import com.mwalimubank.mbimsapi.features.approval.entity.UserApprovalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserApprovalRepository extends JpaRepository<UserApprovalEntity, Long>, JpaSpecificationExecutor<UserApprovalEntity> {
    UserApprovalEntity findByName(String name);

    Optional<UserApprovalEntity> findBySysApprovalId(Long sysApprovalId);
}