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

    Optional<EmployeeEntity> findByEmail(String email);

    @Modifying
    @Query(value = """
        MERGE INTO MBIMS.EMPLOYEE AS target
        USING (
            SELECT 
                STAFF_NO AS staff_no,
                FIRST_NAME AS first_name,
                FATHER_NAME AS middle_name,
                LAST_NAME AS last_name,
                TRIM(
                    COALESCE(TRIM(FIRST_NAME), '') ||
                    CASE WHEN TRIM(COALESCE(FATHER_NAME, '')) <> '' THEN ' ' || TRIM(FATHER_NAME) ELSE '' END ||
                    CASE WHEN TRIM(COALESCE(LAST_NAME, '')) <> '' THEN ' ' || TRIM(LAST_NAME) ELSE '' END
                ) AS name,
                SEX AS gender,
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
                first_name = source.first_name,
                middle_name = source.middle_name,
                last_name = source.last_name,
                gender = source.gender,
                updated_at = CURRENT_TIMESTAMP
        WHEN NOT MATCHED THEN
            INSERT (staff_no, name, first_name, middle_name, last_name, gender, created_at, updated_at, deleted, version)
            VALUES (source.staff_no, source.name, source.first_name, source.middle_name, source.last_name, source.gender, source.created_at, CURRENT_TIMESTAMP, 0, 0)
        """, nativeQuery = true)
    int syncEmployees();
}
