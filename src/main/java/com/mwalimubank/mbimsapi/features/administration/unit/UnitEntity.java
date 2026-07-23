package com.mwalimubank.mbimsapi.features.administration.unit;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "unit")
public class UnitEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(columnDefinition = "VARCHAR(1000)")
    private String description;

    @Column(name = "manager", nullable = false)
    private String manager;

    @Column(name = "code", nullable = false)
    private String code;
}
