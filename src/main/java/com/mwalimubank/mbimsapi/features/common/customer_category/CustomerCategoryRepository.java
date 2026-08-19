package com.mwalimubank.mbimsapi.features.common.customer_category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerCategoryRepository extends JpaRepository<CustomerCategoryEntity, Long> {

    Page<CustomerCategoryEntity> findAll(Specification<CustomerCategoryEntity> spec, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        MERGE INTO MBIMS.customer_category AS t
        USING (
            SELECT
                s.FK_CUSTOMERCUST_ID     AS customer_id,
                s.FK_CATEGORYCATEGOR     AS category,
                s.FK_GENERIC_DETASER     AS generic_detail_ser,
                s.TMSTAMP                AS tmstamp,
                s.FK_GENERIC_DETAFK      AS generic_detail
            FROM PROFITS.CUSTOMER_CATEGORY s
            WHERE s.FK_CUSTOMERCUST_ID IS NOT NULL
              AND s.FK_CATEGORYCATEGOR IS NOT NULL
        ) AS s
        ON (
            t.customer_id = s.customer_id
            AND t.category    = s.category
        )
        WHEN MATCHED THEN
            UPDATE SET
                generic_detail_ser = s.generic_detail_ser,
                tmstamp            = s.tmstamp,
                generic_detail     = s.generic_detail,
                updated_at         = CURRENT_TIMESTAMP
        WHEN NOT MATCHED THEN
            INSERT (
                customer_id, category,
                generic_detail_ser, tmstamp, generic_detail,
                deleted, created_at, updated_at
            ) VALUES (
                s.customer_id, s.category,
                s.generic_detail_ser, s.tmstamp, s.generic_detail,
                0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            )
        """, nativeQuery = true)
    int upsertAllCustomerCategories();
}
