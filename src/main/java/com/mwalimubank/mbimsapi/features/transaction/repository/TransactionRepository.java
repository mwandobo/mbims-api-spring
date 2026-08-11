package com.mwalimubank.mbimsapi.features.transaction.repository;

import com.mwalimubank.mbimsapi.features.transaction.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    Page<TransactionEntity> findAll(Specification<TransactionEntity> spec, Pageable pageable);


    /**
     * Pure SQL bulk upsert – no duplicates.
     * Adjust the ON CONFLICT / MERGE syntax according to your DB
     * (PostgreSQL example shown, Oracle version also provided below)
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
    MERGE INTO MBIMS.transaction AS t
    USING (
        SELECT
            s.FK_UNITCODETRXUNIT   AS unit_id,
            s.FK_USRCODE           AS user_code,
            s.LINE_NUM             AS line_num,
            CAST(s.CUST_ID AS VARCHAR(20)) AS customer_id,
            s.TRN_DATE             AS trx_date,
            s.TRN_SNUM             AS trx_snum,
            s.TRX_SN               AS trx_sn,
            s.TRX_CODE             AS trx_code,
            s.ID_PRODUCT           AS product_id,
            s.ACCOUNT_NUMBER       AS account_number,
            s.DC_AMOUNT            AS dc_amount,
            s.FC_AMOUNT            AS fc_amount,
            s.AVAILABILITY_DATE    AS availability_date,
            s.TMSTAMP              AS tmstamp,
            s.CURRENCY_SHORT_DES   AS currency,
            s.TRX_USR              AS trx_user_code,
            COALESCE(s.EXTERNAL_GLACCOUNT, s.FK_GLG_ACCOUNTACCO) AS gl_account_code,
            s.JUSTIFIC_DESCR       AS justification,
            s.SUBSYSTEM            AS subsystem
        FROM PROFITS.GLI_TRX_EXTRACT s
            WHERE  s.FK_GLG_ACCOUNTACCO LIKE '2.3.0.00.0087%'
               OR s.FK_GLG_ACCOUNTACCO LIKE '1.4.4.00.0063%'
               OR s.FK_GLG_ACCOUNTACCO LIKE '2.3.0.00.0064%'
               OR s.FK_GLG_ACCOUNTACCO LIKE '5.0.4.04.0001%'
               OR s.FK_GLG_ACCOUNTACCO LIKE '5.0.4.04.0002%'
               OR s.FK_GLG_ACCOUNTACCO LIKE '1.4.4.00.0046%'
               OR s.FK_GLG_ACCOUNTACCO LIKE '1.4.4.00.0074%'
               OR s.FK_GLG_ACCOUNTACCO LIKE '2.3.0.00.0123%'
    ) AS s
    ON (
        t.unit_id   = s.unit_id
        AND t.user_code = s.user_code
        AND t.line_num  = s.line_num
        AND t.trx_date  = s.trx_date
        AND t.trx_snum  = s.trx_snum
    )
    WHEN MATCHED THEN
        UPDATE SET
            customer_id       = s.customer_id,
            trx_sn            = s.trx_sn,
            trx_code          = s.trx_code,
            product_id        = s.product_id,
            account_number    = s.account_number,
            dc_amount         = s.dc_amount,
            fc_amount         = s.fc_amount,
            availability_date = s.availability_date,
            tmstamp           = s.tmstamp,
            currency          = s.currency,
            trx_user_code     = s.trx_user_code,
            gl_account_code   = s.gl_account_code,
            justification     = s.justification,
            sub_system         = s.subsystem,
            updated_at        = CURRENT_TIMESTAMP
    WHEN NOT MATCHED THEN
        INSERT (
            unit_id, user_code, line_num, customer_id, trx_date, trx_snum,
            trx_sn, trx_code, product_id, account_number, dc_amount, fc_amount,
            availability_date, tmstamp, currency, trx_user_code,
            gl_account_code, justification, sub_system, created_at, updated_at
        ) VALUES (
            s.unit_id, s.user_code, s.line_num, s.customer_id, s.trx_date, s.trx_snum,
            s.trx_sn, s.trx_code, s.product_id, s.account_number, s.dc_amount, s.fc_amount,
            s.availability_date, s.tmstamp, s.currency, s.trx_user_code,
            s.gl_account_code, s.justification, s.subsystem,
            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
        )
    """, nativeQuery = true)
    int upsertAllRelevantTransactions();
}
