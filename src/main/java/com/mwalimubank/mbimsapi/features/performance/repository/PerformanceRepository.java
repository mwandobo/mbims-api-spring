package com.mwalimubank.mbimsapi.features.performance.repository;

import com.mwalimubank.mbimsapi.features.performance.entities.PerformanceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PerformanceRepository extends JpaRepository<PerformanceEntity, Long> {
    Optional<PerformanceEntity> findByName(String name);

    Page<PerformanceEntity> findAll(Specification<PerformanceEntity> spec, Pageable pageable);
}
