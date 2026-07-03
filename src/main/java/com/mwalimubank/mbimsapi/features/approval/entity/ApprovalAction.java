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
@Table(name = "mbimsapi_approval_actions")
public class ApprovalAction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING) // Store enum as text in DB (better readability than ORDINAL)
    @Column(nullable = false)
    private ApprovalActionEnum action = ApprovalActionEnum.PENDING; // default value

    @Enumerated(EnumType.STRING) // Store enum as text in DB (better readability than ORDINAL)
    @Column(nullable = false)
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
    private ApprovalLevel approvalLevel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_approval_id")
    private UserApproval userApproval;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING) // Store enum as text in DB (better readability than ORDINAL)
    @Column(nullable = false)
    private StatusEnum status = StatusEnum.PENDING; // default value
}
