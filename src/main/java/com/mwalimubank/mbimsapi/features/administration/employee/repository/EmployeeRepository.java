package com.mwalimubank.mbimsapi.features.administration.employee.repository;

import com.mwalimubank.mbimsapi.features.administration.employee.entity.EmployeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    Optional<EmployeeEntity> findByName(String name);

    Page<EmployeeEntity> findAll(
            Specification<EmployeeEntity> spec,
            Pageable pageable
    );
}
