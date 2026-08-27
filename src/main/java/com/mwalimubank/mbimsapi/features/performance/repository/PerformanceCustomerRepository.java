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


    /**
     * Returns top units by number of customers.
     * Each row: [unitId, unitName, totalCustomers, active, dormant, closed]
     */
    @Query(value = """
    SELECT * FROM (
        SELECT 
            u.id AS unit_id,
            u.name AS unit_name,
            COUNT(c.id) AS total_customers,
            SUM(CASE WHEN c.cust_status = '2' THEN 1 ELSE 0 END) AS active,
            SUM(CASE WHEN c.cust_status = '1' THEN 1 ELSE 0 END) AS dormant,
            SUM(CASE WHEN c.cust_status IN ('S', 'SUSPENDED') OR c.cust_status IS NULL THEN 1 ELSE 0 END) AS closed,
            ROW_NUMBER() OVER (ORDER BY COUNT(c.id) DESC) AS rn
        FROM MBIMS.customer c
        INNER JOIN MBIMS.employee e ON e.staff_no = c.employee_code
        INNER JOIN MBIMS.unit u ON u.id = e.unit_id
        WHERE c.deleted = 0
          AND e.deleted = 0
          AND u.deleted = 0
          AND c.employee_code IS NOT NULL
        GROUP BY u.id, u.name
    ) AS ranked
    WHERE ranked.rn <= :limit
    """, nativeQuery = true)
    List<Object[]> findTopUnitsByCustomerCount(@Param("limit") int limit);

    /**
     * Same as above but without limit (for "Show More" page)
     */
    @Query(value = """
        SELECT 
            u.id AS unit_id,
            u.name AS unit_name,
            COUNT(c.id) AS total_customers,
            SUM(CASE WHEN c.cust_status = '2' THEN 1 ELSE 0 END) AS active,
            SUM(CASE WHEN c.cust_status = '1' THEN 1 ELSE 0 END) AS dormant,
            SUM(CASE WHEN c.cust_status IN ('S', 'SUSPENDED') OR c.cust_status IS NULL THEN 1 ELSE 0 END) AS closed
        FROM MBIMS.customer c
        INNER JOIN MBIMS.employee e ON e.staff_no = c.employee_code
        INNER JOIN MBIMS.unit u ON u.id = e.unit_id
        WHERE c.deleted = 0
          AND e.deleted = 0
          AND u.deleted = 0
          AND c.employee_code IS NOT NULL
        GROUP BY u.id, u.name
        ORDER BY total_customers DESC
        """, nativeQuery = true)
    List<Object[]> findAllUnitsByCustomerCount();



}
