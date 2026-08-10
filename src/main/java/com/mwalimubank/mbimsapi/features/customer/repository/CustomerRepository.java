package com.mwalimubank.mbimsapi.features.customer.repository;

import com.mwalimubank.mbimsapi.features.customer.entity.CustomerEntity;
import com.mwalimubank.mbimsapi.features.customer.entity.CustomerFromBankEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByName(String name);

    List<CustomerEntity> findByCustType(String custType);

    Optional<CustomerEntity> findByCustId(Long custId);

    Page<CustomerEntity> findAll(Specification<CustomerEntity> spec, Pageable pageable);
}
