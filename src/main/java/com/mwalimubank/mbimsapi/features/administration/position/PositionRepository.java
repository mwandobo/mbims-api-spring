package com.mwalimubank.mbimsapi.features.administration.position;

import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PositionRepository extends JpaRepository<PositionEntity, Long> , JpaSpecificationExecutor<PositionEntity> {
    Optional<PositionEntity> findByName(String name);
    Optional<PositionEntity> findByNameAndDepartmentId(String name, Long departmentId);
}
