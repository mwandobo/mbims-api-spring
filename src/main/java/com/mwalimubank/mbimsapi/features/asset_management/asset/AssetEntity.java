package com.mwalimubank.mbimsapi.features.asset_management.asset;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import com.mwalimubank.mbimsapi.features.asset_management.assetcategory.AssetCategoryEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "mbimsapi_asset")
public class AssetEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_category_id")
    private AssetCategoryEntity assetcategory;

}
