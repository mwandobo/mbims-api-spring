package com.mwalimubank.mbimsapi.features.asset_management.asset_request;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import com.mwalimubank.mbimsapi.features.asset_management.requested_item.RequestedItemEntity;
import com.mwalimubank.mbimsapi.features.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "asset_request")
public class AssetRequestEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String name;

    @Column()
    private String description;

    @Column(nullable = false)
    private Integer status = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "created_by", nullable = true)
    private UserEntity createdBy;

    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY)
    private List<RequestedItemEntity> items;
}
