package com.mwalimubank.mbimsapi.features.administration.employee;

import com.mwalimubank.mbimsapi.features.administration.employee.dto.EmployeeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String> {



    @Query(value = """
    SELECT 
        TRIM(
            COALESCE(TRIM(be.FIRST_NAME), '') ||
            CASE WHEN TRIM(COALESCE(be.FATHER_NAME, '')) <> '' THEN ' ' || TRIM(be.FATHER_NAME) ELSE '' END ||
            CASE WHEN TRIM(COALESCE(be.LAST_NAME, '')) <> '' THEN ' ' || TRIM(be.LAST_NAME) ELSE '' END
        ) AS empName,
        CASE 
            WHEN be.SEX = 'M' THEN 'Male'
            WHEN be.SEX = 'F' THEN 'Female'
            ELSE 'Not Applicable' 
        END AS gender,
        be.STAFF_NO AS staffNo,
        VARCHAR_FORMAT(be.TMSTAMP, 'DD/MM/YYYY') AS createdDate
    FROM BANKEMPLOYEE be
    WHERE be.STAFF_NO IS NOT NULL
      AND TRIM(be.STAFF_NO) = be.STAFF_NO
      AND be.EMPL_STATUS = 1
      AND be.STAFF_NO LIKE 'EIC%'
    """, nativeQuery = true)
    Page<Object[]> findAllActiveEICEmployees(Pageable pageable);


    @Query(value = """
            SELECT 
                TRIM(
                    COALESCE(TRIM(be.FIRST_NAME), '') ||
                    CASE WHEN TRIM(COALESCE(be.FATHER_NAME, '')) <> '' THEN ' ' || TRIM(be.FATHER_NAME) ELSE '' END ||
                    CASE WHEN TRIM(COALESCE(be.LAST_NAME, '')) <> '' THEN ' ' || TRIM(be.LAST_NAME) ELSE '' END
                ) AS empName,
                CASE 
                    WHEN be.SEX = 'M' THEN 'Male'
                    WHEN be.SEX = 'F' THEN 'Female'
                    ELSE 'Not Applicable' 
                END AS gender,
                be.STAFF_NO AS staffNo,
                VARCHAR_FORMAT(be.TMSTAMP, 'DD/MM/YYYY') AS createdDate
            FROM BANKEMPLOYEE be
            WHERE be.STAFF_NO IS NOT NULL
              AND TRIM(be.STAFF_NO) = be.STAFF_NO
              AND be.EMPL_STATUS = 1
              AND be.STAFF_NO LIKE 'EIC%'
AND (
          UPPER(be.FIRST_NAME) LIKE UPPER('%' || :search || '%')
          OR UPPER(be.FATHER_NAME) LIKE UPPER('%' || :search || '%')
          OR UPPER(be.LAST_NAME) LIKE UPPER('%' || :search || '%')
          OR UPPER(be.STAFF_NO) LIKE UPPER('%' || :search || '%')
      )
            """, nativeQuery = true)
    Page<Object[]> findAllActiveEICEmployeesWithSearch(@Param("search") String search, Pageable pageable);


    }
