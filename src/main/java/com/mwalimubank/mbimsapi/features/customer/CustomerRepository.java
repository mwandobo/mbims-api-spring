package com.mwalimubank.mbimsapi.features.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByFirstName(String name);
    List<CustomerEntity> findByCustType(String custType);
    Page<CustomerEntity> findAll( Pageable pageable);
}
