package com.mwalimubank.mbimsapi.features.administration.unit;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import com.mwalimubank.mbimsapi.features.administration.employee.entity.EmployeeEntity;
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

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "manager_id", nullable = true)
    private EmployeeEntity manager;

    @Column(name = "code", nullable = false)
    private String code;
}
