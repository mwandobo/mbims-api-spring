package com.mwalimubank.mbimsapi.features.administration.unit;

import com.mwalimubank.mbimsapi.features.administration.position.PositionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UnitRepository extends JpaRepository<UnitEntity, Long> , JpaSpecificationExecutor<UnitEntity> {
    Optional<UnitEntity> findByName(String name);
}
