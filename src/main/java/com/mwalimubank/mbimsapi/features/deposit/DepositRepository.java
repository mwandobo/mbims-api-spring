package com.mwalimubank.mbimsapi.features.deposit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DepositRepository extends JpaRepository<DepositEntity, Long> , JpaSpecificationExecutor<DepositEntity>{

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
    MERGE INTO MBIMS.deposit AS t
    USING (
        SELECT
            s.FK_UNITCODETRXUNIT   AS unit_id,
            s.FK_USRCODE           AS user_code,
            s.LINE_NUM             AS line_num,
            c.id                   AS customer_id,
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
        LEFT JOIN MBIMS.customer c
            ON CAST(c.cust_id AS VARCHAR(20)) = CAST(s.CUST_ID AS VARCHAR(20))
        WHERE (
               s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.01.0003%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.01.0005%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.01.0006%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.01.0012%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.01.0014%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.01.0015%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.01.0016%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.01.0017%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.01.0018%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.01.0021%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.02.0002%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.02.0007%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.21.0001%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.04.0001%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.04.0002%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.04.0003%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.04.0004%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.04.0005%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.0.04.0006%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.1.00.0002%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.1.00.0015%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.1.00.0020%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.2.00.0001%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.2.00.0003%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.2.00.0004%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.2.00.0008%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.2.00.0017%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.2.00.0022%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.2.00.0023%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.2.00.0026%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.2.00.0027%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.2.00.0029%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.2.00.0031%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.2.00.0034%'
            OR s.FK_GLG_ACCOUNTACCO LIKE '2.0.2.00.0037%'
        )
    ) AS s
    ON (
        t.unit_id     = s.unit_id
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
            sub_system        = s.subsystem,
            updated_at        = CURRENT_TIMESTAMP,
                deleted    = 0
    WHEN NOT MATCHED THEN
        INSERT (
            unit_id, user_code, line_num, customer_id, trx_date, trx_snum,
            trx_sn, trx_code, product_id, account_number, dc_amount, fc_amount,
            availability_date, tmstamp, currency, trx_user_code,
            gl_account_code, justification, sub_system, created_at, updated_at,deleted
        ) VALUES (
            s.unit_id, s.user_code, s.line_num, s.customer_id, s.trx_date, s.trx_snum,
            s.trx_sn, s.trx_code, s.product_id, s.account_number, s.dc_amount, s.fc_amount,
            s.availability_date, s.tmstamp, s.currency, s.trx_user_code,
            s.gl_account_code, s.justification, s.subsystem,
            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
        )
    """, nativeQuery = true)
    int upsertDepositTransactions();
}







