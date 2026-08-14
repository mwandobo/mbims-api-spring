package com.mwalimubank.mbimsapi.features.common.customer_category;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "customer_category")
public class CustomerCategoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "category", length = 8, nullable = false)
    private String category;

    @Column(name = "generic_detail_ser")
    private Integer genericDetailSer;          // FK_GENERIC_DETASER

    @Column(name = "tmstamp")
    private LocalDateTime tmstamp;

    @Column(name = "generic_detail", length = 50)  // adjust length if needed
    private String genericDetail;              // FK_GENERIC_DETAFK
}
