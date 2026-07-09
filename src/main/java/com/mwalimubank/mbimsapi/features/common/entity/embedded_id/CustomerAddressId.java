package com.mwalimubank.mbimsapi.features.common.entity.embedded_id;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class CustomerAddressId implements Serializable {

    private Integer fkCustomerCustId;

    private Short serialNum;
}