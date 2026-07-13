package com.mwalimubank.mbimsapi.features.common.entity.embedded_id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class CustomerCategoryId implements Serializable {

    @Column(name = "FK_CUSTOMERCUST_ID")
    private Integer fkCustomerCustId;

    @Column(name = "FK_CATEGORYCATEGOR", length = 8)
    private String category;
}