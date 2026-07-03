package com.mwalimubank.mbimsapi.features.permission;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "mbimsapi_permissions")
public class PermissionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column()
    private String group;

    @Column()
    private String description;
}
