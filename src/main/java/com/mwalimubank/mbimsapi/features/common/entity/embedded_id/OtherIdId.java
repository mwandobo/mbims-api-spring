package com.mwalimubank.mbimsapi.features.common.entity.embedded_id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class OtherIdId implements Serializable {

    @Column(name = "FK_CUSTOMERCUST_ID")
    private Integer fkCustomerCustId;

    @Column(name = "SERIAL_NO")
    private Short serialNo;
}