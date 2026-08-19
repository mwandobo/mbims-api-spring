package com.mwalimubank.mbimsapi.features.performance.repository;

import com.mwalimubank.mbimsapi.features.customer.entity.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PerformanceCustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Page<CustomerEntity> findAll(Specification<CustomerEntity> spec, Pageable pageable);
    List<CustomerEntity> findByCustType(String custType);


    long countByCustType(String custType);

    @Query("""
        SELECT c.custStatus, COUNT(c)
        FROM CustomerEntity c
        GROUP BY c.custStatus
        """)
    List<Object[]> countByStatus();

    @Query("""
        SELECT c.custStatus, COUNT(c)
        FROM CustomerEntity c
        WHERE c.custType = :custType
        GROUP BY c.custStatus
        """)
    List<Object[]> countByStatusAndCustType(@Param("custType") String custType);

    @Query("""
        SELECT c.custStatus, COUNT(c)
        FROM CustomerEntity c
        WHERE c.custType IN ('2', '3')
        GROUP BY c.custStatus
        """)
    List<Object[]> countByStatusCorporate();




}
