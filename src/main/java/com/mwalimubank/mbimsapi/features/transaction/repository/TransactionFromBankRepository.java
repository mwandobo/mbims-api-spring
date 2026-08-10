package com.mwalimubank.mbimsapi.features.transaction.repository;

import com.mwalimubank.mbimsapi.features.transaction.entity.TransactionFromBankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionFromBankRepository extends JpaRepository<TransactionFromBankEntity, Long> {

    @Query(value = """
        SELECT *
        FROM PROFITS.GLI_TRX_EXTRACT
        WHERE CUST_ID = :custId
        AND EXTERNAL_GLACCOUNT IN ('230000087','144000063','144000074','230000123')
        """, nativeQuery = true)
    List<TransactionFromBankEntity> findByCustId(Integer custId);

}
