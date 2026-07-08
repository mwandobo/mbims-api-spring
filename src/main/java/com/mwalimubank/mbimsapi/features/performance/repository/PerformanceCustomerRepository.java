package com.mwalimubank.mbimsapi.features.performance.repository;

import com.mwalimubank.mbimsapi.features.customer.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceCustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Page<CustomerEntity> findAll(Specification<CustomerEntity> spec, Pageable pageable);
    List<CustomerEntity> findByCustType(String custType);
}
