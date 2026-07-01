package com.mwalimubank.mbimsapi.features.asset_management.assetcategory;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "assetcategory")
public class AssetCategoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column
    private String description;
}
