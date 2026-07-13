package com.mwalimubank.mbimsapi.features.common.repository;


import com.mwalimubank.mbimsapi.features.common.entity.CustomerAddressEntity;
import com.mwalimubank.mbimsapi.features.common.entity.GenericDetailEntity;
import com.mwalimubank.mbimsapi.features.common.entity.embedded_id.CustomerAddressId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenericDetailRepository extends JpaRepository<GenericDetailEntity, String> {

    Optional<GenericDetailEntity> findFirstByIdAndSerialNumber(
            String generic_header,
            Integer serial_num
    );
}