package com.mwalimubank.mbimsapi.features.administration.employee.repository;

import com.mwalimubank.mbimsapi.features.administration.employee.entity.EmployeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    Page<EmployeeEntity> findAll(
            Specification<EmployeeEntity> spec,
            Pageable pageable
    );

    @Modifying
    @Query(value = """
        MERGE INTO MBIMS.EMPLOYEE AS target
        USING (
            SELECT 
                STAFF_NO AS staff_no,
                TRIM(
                    COALESCE(TRIM(FIRST_NAME), '') ||
                    CASE WHEN TRIM(COALESCE(FATHER_NAME, '')) <> '' THEN ' ' || TRIM(FATHER_NAME) ELSE '' END ||
                    CASE WHEN TRIM(COALESCE(LAST_NAME, '')) <> '' THEN ' ' || TRIM(LAST_NAME) ELSE '' END
                ) AS name,
                CASE 
                    WHEN SEX = 'M' THEN 'Male'
                    WHEN SEX = 'F' THEN 'Female'
                    ELSE 'Not Applicable'
                END AS gender,
                TMSTAMP AS created_at
            FROM PROFITS.BANKEMPLOYEE
            WHERE STAFF_NO IS NOT NULL
              AND TRIM(STAFF_NO) = STAFF_NO
              AND EMPL_STATUS = 1
              AND STAFF_NO LIKE 'EIC%'
        ) AS source
        ON target.staff_no = source.staff_no
        WHEN MATCHED THEN
            UPDATE SET 
                name = source.name,
                gender = source.gender,
                updated_at = CURRENT_TIMESTAMP
        WHEN NOT MATCHED THEN
            INSERT (staff_no, name, gender, created_at, updated_at, deleted, version)
            VALUES (source.staff_no, source.name, source.gender, source.created_at, CURRENT_TIMESTAMP, 0, 0)
        """, nativeQuery = true)
    int syncEmployees();
}
