package com.mwalimubank.mbimsapi.features.transaction.repository;

import com.mwalimubank.mbimsapi.features.transaction.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    Page<TransactionEntity> findAll(Specification<TransactionEntity> spec, Pageable pageable);
}
