package com.mwalimubank.mbimsapi.features.common.entity;

import com.mwalimubank.mbimsapi.features.common.entity.embedded_id.CustomerCategoryId;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(schema = "profits", name = "customer_category")
public class CustomerCategoryEntity {

    @EmbeddedId
    private CustomerCategoryId id;


    @Column(name = "FK_GENERIC_DETASER")
    private Integer fkGenericDetaSer;


    @Column(name = "TMSTAMP")
    private LocalDateTime tmstamp;


    @Column(name = "FK_GENERIC_DETAFK")
    private String genericDetail;
}