package com.mwalimubank.mbimsapi.features.administration.employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeSyncService {

    private final JdbcTemplate jdbcTemplate;   // single DB2 connection
    /**
     * Syncs data from profits.BANKEMPLOYEE → mbims.employees
     * Only inserts new records and updates existing ones.
     */
    @Transactional
    public int syncEmployees() {
        log.info("Starting employee sync from PROFITS.BANKEMPLOYEE → MBIMS.EMPLOYEE...");

        String sql = """
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
    """;

        int affected = jdbcTemplate.update(sql);
        log.info("Employee sync completed. Rows affected: {}", affected);
        return affected;
    }
}