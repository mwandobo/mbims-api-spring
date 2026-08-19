package com.mwalimubank.mbimsapi.features.common.other_id;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OtherIdRepository extends JpaRepository<OtherIdEntity, Long> {

    Page<OtherIdEntity> findAll(Specification<OtherIdEntity> spec, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        MERGE INTO MBIMS.other_id AS t
        USING (
            SELECT
                s.FK_CUSTOMERCUST_ID     AS customer_id,
                s.SERIAL_NO              AS serial_no,
                s.FKGD_HAS_TYPE          AS id_type_id,
                s.FKGD_HAS_BEEN_ISSU     AS issued_by_id,
                s.EXPIRY_DATE            AS expiry_date,
                s.TMSTAMP                AS tmstamp,
                s.ISSUE_DATE             AS issue_date,
                s.MAIN_FLAG              AS main_flag,
                s.FKGH_HAS_BEEN_ISSU     AS issued_by_code,
                s.FKGH_HAS_TYPE          AS id_type_code,
                s.ID_NO                  AS id_no,
                s.ISSUE_AUTHORITY        AS issue_authority,
                s.INCOMPLETE_U_COMNT     AS incomplete_comment
            FROM PROFITS.OTHER_ID s
            WHERE s.FK_CUSTOMERCUST_ID IS NOT NULL
        ) AS s
        ON (
            t.customer_id = s.customer_id
            AND t.serial_no   = s.serial_no
        )
        WHEN MATCHED THEN
            UPDATE SET
                id_type_id         = s.id_type_id,
                issued_by_id       = s.issued_by_id,
                expiry_date        = s.expiry_date,
                tmstamp            = s.tmstamp,
                issue_date         = s.issue_date,
                main_flag          = s.main_flag,
                issued_by_code     = s.issued_by_code,
                id_type_code       = s.id_type_code,
                id_no              = s.id_no,
                issue_authority    = s.issue_authority,
                incomplete_comment = s.incomplete_comment,
                updated_at         = CURRENT_TIMESTAMP
        WHEN NOT MATCHED THEN
            INSERT (
                customer_id, serial_no,
                id_type_id, issued_by_id, expiry_date, tmstamp, issue_date,
                main_flag, issued_by_code, id_type_code, id_no,
                issue_authority, incomplete_comment,
                deleted, created_at, updated_at
            ) VALUES (
                s.customer_id, s.serial_no,
                s.id_type_id, s.issued_by_id, s.expiry_date, s.tmstamp, s.issue_date,
                s.main_flag, s.issued_by_code, s.id_type_code, s.id_no,
                s.issue_authority, s.incomplete_comment,
                0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            )
        """, nativeQuery = true)
    int upsertAllOtherIds();
}
