package com.mwalimubank.mbimsapi.features.common.repository;


import com.mwalimubank.mbimsapi.features.common.entity.CustomerCategoryEntity;
import com.mwalimubank.mbimsapi.features.common.entity.embedded_id.CustomerCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerCategoryRepository extends JpaRepository<CustomerCategoryEntity, CustomerCategoryId> {

    Optional<CustomerCategoryEntity> findFirstByIdFkCustomerCustIdAndIdCategoryAndGenericDetail(
            Integer customerId,
            String category,
            String genericDetail
    );
}