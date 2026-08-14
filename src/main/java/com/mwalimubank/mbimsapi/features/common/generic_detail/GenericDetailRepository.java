package com.mwalimubank.mbimsapi.features.common.generic_detail;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GenericDetailRepository extends JpaRepository<GenericDetailEntity, Long> {
    Optional<GenericDetailEntity> findByName(String name);

    Page<GenericDetailEntity> findAll(Specification<GenericDetailEntity> spec, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        MERGE INTO MBIMS.generic_parameter AS t
        USING (
            SELECT
                s.FK_GENERIC_HEADPAR   AS parameter_code,
                s.DESCRIPTION          AS description,
                s.SHORT_DESCRIPTION    AS short_description,
                s.PARAMETER_TYPE       AS parameter_type,
                s.SERIAL_NUM           AS serial_number
            FROM PROFITS.GENERIC_HEAD s
            WHERE s.FK_GENERIC_HEADPAR IS NOT NULL
        ) AS s
        ON (t.parameter_code = s.parameter_code)
        WHEN MATCHED THEN
            UPDATE SET
                description       = s.description,
                short_description = s.short_description,
                parameter_type    = s.parameter_type,
                serial_number     = s.serial_number,
                updated_at        = CURRENT_TIMESTAMP
        WHEN NOT MATCHED THEN
            INSERT (
                parameter_code, description, short_description,
                parameter_type, serial_number,
                deleted, created_at, updated_at
            ) VALUES (
                s.parameter_code, s.description, s.short_description,
                s.parameter_type, s.serial_number,
                0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            )
        """, nativeQuery = true)
    int upsertAllGenericParameters();
}
