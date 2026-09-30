package com.mwalimubank.mbimsapi.features.approval.repository;

import com.mwalimubank.mbimsapi.features.approval.entity.SysApprovalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SysApprovalRepository  extends JpaRepository<SysApprovalEntity, Long> {
    SysApprovalEntity findByName(String name);

    Page<SysApprovalEntity> findAll(Specification<SysApprovalEntity> spec, Pageable pageable);

    Optional<SysApprovalEntity> findByEntityName(String entityName);
}