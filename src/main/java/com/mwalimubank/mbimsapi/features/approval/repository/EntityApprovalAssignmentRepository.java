package com.mwalimubank.mbimsapi.features.approval.repository;

import com.mwalimubank.mbimsapi.features.approval.entity.EntityApprovalAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntityApprovalAssignmentRepository
        extends JpaRepository<EntityApprovalAssignmentEntity, Long> {

    Optional<EntityApprovalAssignmentEntity>
    findFirstByEntityNameAndEntityIdAndDeletedFalseOrderByIdDesc(
            String entityName, Long entityId);
}