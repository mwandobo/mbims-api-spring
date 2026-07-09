package com.mwalimubank.mbimsapi.features.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(schema = "profits", name = "generic_detail")
public class GenericDetailEntity {

    @Id
    @Column(name = "FK_GENERIC_HEADPAR")
    private String id;

    @Column(name = "description", length = 40)
    private String description;

    @Column(name = "short_description", length = 40)
    private String shortDescription;

    @Column(name = "parameter_type")
    private String parameterType;
}
