package com.mwalimubank.mbimsapi.features.transaction;

import com.mwalimubank.mbimsapi.features.transaction.entity.TransactionEntity;
import com.mwalimubank.mbimsapi.features.transaction.entity.TransactionId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query(value = """
        SELECT *
        FROM PROFITS.GLI_TRX_EXTRACT
        WHERE CUST_ID = :custId
        AND EXTERNAL_GLACCOUNT IN ('230000087','144000063','144000074','230000123')
        """, nativeQuery = true)
    List<TransactionEntity> findByCustId(Integer custId);

}
