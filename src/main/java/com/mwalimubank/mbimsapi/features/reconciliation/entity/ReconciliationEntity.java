package com.mwalimubank.mbimsapi.features.reconciliation.entity;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reconciliation")
public class ReconciliationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;              // optional label from user
    private String name;              // optional label from user
    private String fileAName;
    private String fileBName;

    private Integer matchCount;
    private Integer missingInACount;
    private Integer missingInBCount;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private UserEntity createdBy;// COMPLETED
}