package com.mwalimubank.mbimsapi.features.reconciliation.entity;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reconciliation_item")
public class ReconciliationItemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reconciliation_id")
    private ReconciliationEntity reconciliation;

    private String value;             // RRN / reference

    /** MATCH | MISSING_IN_A | MISSING_IN_B */
    private String itemType;

    private String sourceFile;        // which file side for missing
}