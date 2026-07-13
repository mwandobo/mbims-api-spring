package com.mwalimubank.mbimsapi.features.common.repository;


import com.mwalimubank.mbimsapi.features.common.entity.CustomerCategoryEntity;
import com.mwalimubank.mbimsapi.features.common.entity.OtherIdEntity;
import com.mwalimubank.mbimsapi.features.common.entity.embedded_id.CustomerCategoryId;
import com.mwalimubank.mbimsapi.features.common.entity.embedded_id.OtherIdId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OtherIdRepository extends JpaRepository<OtherIdEntity, OtherIdId> {

    @Query("""
        SELECT o\s
        FROM OtherIdEntity o
        WHERE o.id.fkCustomerCustId = :customerId
        AND (o.id.serialNo IS NULL OR o.mainFlag = '1')
       \s""")
    Optional<OtherIdEntity> findMainIdByCustomerId(Integer customerId);
}