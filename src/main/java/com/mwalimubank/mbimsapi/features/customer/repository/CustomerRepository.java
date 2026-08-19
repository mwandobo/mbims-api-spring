package com.mwalimubank.mbimsapi.features.customer.repository;

import com.mwalimubank.mbimsapi.features.customer.entity.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    List<CustomerEntity> findByCustType(String custType);

    Optional<CustomerEntity> findByCustId(Long custId);

    Page<CustomerEntity> findAll(Specification<CustomerEntity> spec, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        MERGE INTO MBIMS.customer AS t
        USING (
            SELECT
                s.CUST_ID,
                s.FIRST_NAME,
                s.MIDDLE_NAME,
                s.SURNAME,
                TRIM(
                    COALESCE(TRIM(s.FIRST_NAME), '') || ' ' ||
                    COALESCE(TRIM(s.MIDDLE_NAME), '') || ' ' ||
                    COALESCE(TRIM(s.SURNAME), '')
                ) AS full_name,
                s.SEX,
                s.DATE_OF_BIRTH,
                s.MOBILE_TEL,
                s.E_MAIL,
                s.CHILDREN_ABOVE18,
                s.NUM_OF_CHILDREN,
                s.FAMILY_MEMBERS,
                s.BIRTHPLACE,
                s.EMPLOYER,
                s.EMPLOYER_ADDRESS,
                s.DAI_NUMBER,
                s.FK_BISS_CODE,
                s.NON_RESIDENT,
                s.VIP_IND,
                s.BLACKLISTED_IND,
                s.CUSTOMER_BEGIN_DAT,
                s.CUST_OPEN_DATE,
                s.TITLE,
                s.TMSTAMP,
                s.CUST_TYPE,
                s.CUST_STATUS
            FROM PROFITS.CUSTOMER s
            WHERE s.CUST_ID IS NOT NULL
        ) AS s
        ON (t.cust_id = s.CUST_ID)
        WHEN MATCHED THEN
            UPDATE SET
                first_name            = s.FIRST_NAME,
                middle_name           = s.MIDDLE_NAME,
                last_name             = s.SURNAME,
                name                  = s.full_name,
                sex                   = s.SEX,
                date_of_birth         = s.DATE_OF_BIRTH,
                phone_number          = s.MOBILE_TEL,
                email                 = s.E_MAIL,
                children_above18      = s.CHILDREN_ABOVE18,
                number_of_children    = s.NUM_OF_CHILDREN,
                family_members        = s.FAMILY_MEMBERS,
                birth_region          = s.BIRTHPLACE,
                employer              = s.EMPLOYER,
                employer_address      = s.EMPLOYER_ADDRESS,
                identification_number = s.DAI_NUMBER,
                identification_type   = s.FK_BISS_CODE,
                non_resident          = s.NON_RESIDENT,
                vip_indicator         = s.VIP_IND,
                blacklisted           = s.BLACKLISTED_IND,
                customer_begin_date   = s.CUSTOMER_BEGIN_DAT,
                customer_open_date    = s.CUST_OPEN_DATE,
                title                 = s.TITLE,
                created_at            = s.TMSTAMP,
                cust_type             = s.CUST_TYPE,
                cust_status           = s.CUST_STATUS,
                updated_at            = CURRENT_TIMESTAMP
        WHEN NOT MATCHED THEN
            INSERT (
                cust_id, first_name, middle_name, last_name, name,
                sex, date_of_birth, phone_number, email,
                children_above18, number_of_children, family_members, birth_region,
                employer, employer_address,
                identification_number, identification_type,
                non_resident, vip_indicator, blacklisted,
                customer_begin_date, customer_open_date, title,
                created_at, cust_type, cust_status,
                deleted, created_at, updated_at
            ) VALUES (
                s.CUST_ID, s.FIRST_NAME, s.MIDDLE_NAME, s.SURNAME, s.full_name,
                s.SEX, s.DATE_OF_BIRTH, s.MOBILE_TEL, s.E_MAIL,
                s.CHILDREN_ABOVE18, s.NUM_OF_CHILDREN, s.FAMILY_MEMBERS, s.BIRTHPLACE,
                s.EMPLOYER, s.EMPLOYER_ADDRESS,
                s.DAI_NUMBER, s.FK_BISS_CODE,
                s.NON_RESIDENT, s.VIP_IND, s.BLACKLISTED_IND,
                s.CUSTOMER_BEGIN_DAT, s.CUST_OPEN_DATE, s.TITLE,
                s.TMSTAMP, s.CUST_TYPE, s.CUST_STATUS,
                0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            )
        """, nativeQuery = true)
    int upsertAllCustomers();

}
