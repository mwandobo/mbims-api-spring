package com.mwalimubank.mbimsapi.features.common.generic_detail;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "generic_detail")
public class GenericDetailEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Original PK from PROFITS (FK_GENERIC_HEADPAR) */
    @Column(name = "parameter_code", length = 20, nullable = false)
    private String parameterCode;

    @Column(name = "description", length = 40)
    private String description;

    @Column(name = "short_description", length = 40)
    private String shortDescription;

    @Column(name = "parameter_type", length = 20)
    private String parameterType;

    @Column(name = "serial_number")
    private Integer serialNumber;
}
