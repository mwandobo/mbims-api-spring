package com.mwalimubank.mbimsapi.features.common.repository;


import com.mwalimubank.mbimsapi.features.common.entity.CustomerAddressEntity;
import com.mwalimubank.mbimsapi.features.common.entity.embedded_id.CustomerAddressId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddressEntity, CustomerAddressId> {

    Optional<CustomerAddressEntity> findFirstByIdFkCustomerCustIdAndCommunicationAddrAndEntryStatus(
            Integer customerId,
            String communicationAddr,
            String entryStatus
    );
}