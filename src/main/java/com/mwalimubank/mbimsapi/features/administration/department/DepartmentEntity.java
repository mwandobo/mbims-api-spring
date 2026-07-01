package com.mwalimubank.mbimsapi.features.administration.department;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "department")
public class DepartmentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column
    private String description;
}
