package com.mwalimubank.mbimsapi.features.reconciliation.entity;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reconciliation")
public class ReconciliationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;              // optional label from user
    private String fileAName;
    private String fileBName;

    private Integer matchCount;
    private Integer missingInACount;
    private Integer missingInBCount;

    private String status;            // COMPLETED
    private Long createdBy;           // current user id (optional)
}