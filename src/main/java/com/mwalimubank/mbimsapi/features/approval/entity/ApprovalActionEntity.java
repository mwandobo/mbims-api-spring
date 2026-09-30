package com.mwalimubank.mbimsapi.features.approval.entity;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import com.mwalimubank.mbimsapi.features.approval.enums.ApprovalActionCreationTypeEnum;
import com.mwalimubank.mbimsapi.features.approval.enums.ApprovalActionEnum;
import com.mwalimubank.mbimsapi.features.approval.enums.StatusEnum;
import com.mwalimubank.mbimsapi.features.role.RoleEntity;
import com.mwalimubank.mbimsapi.features.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "approval_action")
public class ApprovalActionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String name;

    @Enumerated(EnumType.STRING) // Store enum as text in DB (better readability than ORDINAL)
    @Column()
    private ApprovalActionEnum action = ApprovalActionEnum.PENDING; // default value

    @Enumerated(EnumType.STRING) // Store enum as text in DB (better readability than ORDINAL)
    @Column()
    private ApprovalActionCreationTypeEnum type = ApprovalActionCreationTypeEnum.NORMAL; // default value

    @Column()
    private String entityName;

    @Column()
    private Long entityId;

    @Column()
    private Long entityCreatorId;

    @Column()
    private String remark;

    @Column()
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approval_level_id")
    private ApprovalLevelEntity approvalLevel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_approval_id")
    private UserApprovalEntity userApproval;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "role_id", nullable = true)
    private RoleEntity role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING) // Store enum as text in DB (better readability than ORDINAL)
    @Column()
    private StatusEnum status = StatusEnum.PENDING; // default value
}
