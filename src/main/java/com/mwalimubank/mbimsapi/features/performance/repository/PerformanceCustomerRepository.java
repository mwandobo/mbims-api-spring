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

    @Query(value = """
    SELECT unit_id, unit_code, unit_name, customer_count, deposit_total
    FROM (
        SELECT 
            u.id                                    AS unit_id,
            u.code                             AS unit_code,
            u.name                                  AS unit_name,
            COALESCE(c.customer_count, 0)           AS customer_count,
            COALESCE(d.deposit_total, 0)            AS deposit_total,
            ROW_NUMBER() OVER (ORDER BY COALESCE(c.customer_count, 0) DESC, COALESCE(d.deposit_total, 0) DESC) AS rn
        FROM MBIMS.unit u
        LEFT JOIN (
            SELECT 
                unit_code,
                COUNT(*) AS customer_count
            FROM MBIMS.customer
            WHERE deleted = 0
              AND unit_code IS NOT NULL
            GROUP BY unit_code
        ) c ON c.unit_code = u.code
        LEFT JOIN (
            SELECT 
                UNIT_ID,
                SUM(DC_AMOUNT) AS deposit_total
            FROM MBIMS.deposit
            GROUP BY UNIT_ID
        ) d ON d.UNIT_ID = u.code
        WHERE u.deleted = 0
    ) ranked
    WHERE ranked.rn <= :limit
    """, nativeQuery = true)
    List<Object[]> findTopUnitsPerformance(@Param("limit") int limit);


    @Query(value = """
    SELECT 
        u.id                                    AS unit_id,
        u.code                                  AS unit_code,
        u.name                                  AS unit_name,
        COALESCE(c.customer_count, 0)           AS customer_count,
        COALESCE(d.deposit_total, 0)            AS deposit_total
    FROM MBIMS.unit u
    LEFT JOIN (
        SELECT 
            unit_code,
            COUNT(*) AS customer_count
        FROM MBIMS.customer
        WHERE deleted = 0
          AND unit_code IS NOT NULL
        GROUP BY unit_code
    ) c ON c.unit_code = u.code
    LEFT JOIN (
        SELECT 
            UNIT_ID,
            SUM(DC_AMOUNT) AS deposit_total
        FROM MBIMS.deposit
        GROUP BY UNIT_ID
    ) d ON d.UNIT_ID = u.code
    WHERE u.deleted = 0
    ORDER BY customer_count DESC, deposit_total DESC
    """, nativeQuery = true)
    List<Object[]> findAllUnitsPerformance();

}
