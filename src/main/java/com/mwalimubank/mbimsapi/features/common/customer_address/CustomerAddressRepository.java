package com.mwalimubank.mbimsapi.features.common.customer_address;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddressEntity, Long> {

    Page<CustomerAddressEntity> findAll(Specification<CustomerAddressEntity> spec, Pageable pageable);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        MERGE INTO MBIMS.customer_address AS t
        USING (
            SELECT
                s.FK_CUSTOMERCUST_ID      AS customer_id,
                s.SERIAL_NUM              AS serial_num,
                s.FKGD_HAS_COUNTRY        AS country_id,
                s.FKGD_HAS_AS_DISTRI      AS district_id,
                s.TMSTAMP                 AS tmstamp,
                s.COMMUNICATION_ADDR      AS communication_addr,
                s.PTS_IND                 AS pts_ind,
                s.ADDRESS_TYPE            AS address_type,
                s.ENTRY_STATUS            AS entry_status,
                s.LATIN_IND               AS latin_ind,
                s.FKGH_HAS_COUNTRY        AS country_code,
                s.SEGM_FLAGS              AS segm_flags,
                s.FKGH_HAS_AS_DISTRI      AS district_code,
                s.MAIL_BOX                AS mail_box,
                s.ZIP_CODE                AS zip_code,
                s.FAX_NO                  AS fax_no,
                s.TELEPHONE               AS telephone,
                s.CITY                    AS city,
                s.REGION                  AS region,
                s.ADDRESS_1               AS address_1,
                s.ADDRESS_2               AS address_2,
                s.ADDRESS_3               AS address_3,
                s.ADDRESS_4               AS address_4,
                s.ADDRESS_5               AS address_5,
                s.ADDRESS_6               AS address_6,
                s.ENTRY_COMMENTS          AS entry_comments,
                s.FK_CUST_ADDR_PACO       AS paco,
                s.FK_CUST_ADDR_PASN       AS pasn,
                s.ACCOMODATION_DATE       AS accommodation_date,
                s.INTERNET_ADDRESS        AS internet_address,
                s.E_MAIL                  AS email,
                s.TELEPHONE_2             AS telephone_2,
                s.TELEPHONE_3             AS telephone_3
            FROM PROFITS.CUST_ADDRESS s
            WHERE s.FK_CUSTOMERCUST_ID IS NOT NULL
        ) AS s
        ON (
            t.customer_id = s.customer_id
            AND t.serial_num  = s.serial_num
        )
        WHEN MATCHED THEN
            UPDATE SET
                country_id         = s.country_id,
                district_id        = s.district_id,
                tmstamp            = s.tmstamp,
                communication_addr = s.communication_addr,
                pts_ind            = s.pts_ind,
                address_type       = s.address_type,
                entry_status       = s.entry_status,
                latin_ind          = s.latin_ind,
                country_code       = s.country_code,
                segm_flags         = s.segm_flags,
                district_code      = s.district_code,
                mail_box           = s.mail_box,
                zip_code           = s.zip_code,
                fax_no             = s.fax_no,
                telephone          = s.telephone,
                city               = s.city,
                region             = s.region,
                address_1          = s.address_1,
                address_2          = s.address_2,
                address_3          = s.address_3,
                address_4          = s.address_4,
                address_5          = s.address_5,
                address_6          = s.address_6,
                entry_comments     = s.entry_comments,
                paco               = s.paco,
                pasn               = s.pasn,
                accommodation_date = s.accommodation_date,
                internet_address   = s.internet_address,
                email              = s.email,
                telephone_2        = s.telephone_2,
                telephone_3        = s.telephone_3,
                updated_at         = CURRENT_TIMESTAMP
        WHEN NOT MATCHED THEN
            INSERT (
                customer_id, serial_num,
                country_id, district_id, tmstamp,
                communication_addr, pts_ind, address_type, entry_status, latin_ind,
                country_code, segm_flags, district_code, mail_box, zip_code,
                fax_no, telephone, city, region,
                address_1, address_2, address_3, address_4, address_5, address_6,
                entry_comments, paco, pasn, accommodation_date,
                internet_address, email, telephone_2, telephone_3,
                deleted, created_at, updated_at
            ) VALUES (
                s.customer_id, s.serial_num,
                s.country_id, s.district_id, s.tmstamp,
                s.communication_addr, s.pts_ind, s.address_type, s.entry_status, s.latin_ind,
                s.country_code, s.segm_flags, s.district_code, s.mail_box, s.zip_code,
                s.fax_no, s.telephone, s.city, s.region,
                s.address_1, s.address_2, s.address_3, s.address_4, s.address_5, s.address_6,
                s.entry_comments, s.paco, s.pasn, s.accommodation_date,
                s.internet_address, s.email, s.telephone_2, s.telephone_3,
                0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            )
        """, nativeQuery = true)
    int upsertAllCustomerAddresses();
}
