package com.mwalimubank.mbimsapi.features.asset_management.requested_item;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import com.mwalimubank.mbimsapi.features.asset_management.asset.AssetEntity;
import com.mwalimubank.mbimsapi.features.asset_management.asset_request.AssetRequestEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "requested_item")
public class RequestedItemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String name;

    @Column()
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private AssetRequestEntity request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private AssetEntity asset;

    @Column(nullable = false)
    private Integer quantity = 1;
}