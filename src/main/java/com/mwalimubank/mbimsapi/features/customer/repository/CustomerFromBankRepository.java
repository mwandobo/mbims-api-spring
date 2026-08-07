package com.mwalimubank.mbimsapi.features.customer.repository;

import com.mwalimubank.mbimsapi.features.customer.entity.CustomerFromBankEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerFromBankRepository extends JpaRepository<CustomerFromBankEntity, Long> {
    Optional<CustomerFromBankEntity> findByFirstName(String name);
    List<CustomerFromBankEntity> findByCustType(String custType);
    Page<CustomerFromBankEntity> findAll( Pageable pageable);
}
