package com.mwalimubank.mbimsapi.features.administration.unit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UnitRepository extends JpaRepository<UnitEntity, Long> {
    Optional<UnitEntity> findByName(String name);

    Page<UnitEntity> findAll(Specification<UnitEntity> spec, Pageable pageable);
}
