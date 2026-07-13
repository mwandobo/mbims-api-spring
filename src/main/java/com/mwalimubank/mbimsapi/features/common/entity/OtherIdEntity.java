package com.mwalimubank.mbimsapi.features.common.entity;

import com.mwalimubank.mbimsapi.features.common.entity.embedded_id.OtherIdId;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(schema = "profits", name = "other_id")
public class OtherIdEntity {

    @EmbeddedId
    private OtherIdId id;

    @Column(name = "FKGD_HAS_TYPE")
    private Integer fkGdHasType;

    @Column(name = "FKGD_HAS_BEEN_ISSU")
    private Integer fkGdHasBeenIssu;

    @Column(name = "EXPIRY_DATE")
    private LocalDate expiryDate;

    @Column(name = "TMSTAMP")
    private LocalDateTime tmstamp;

    @Column(name = "ISSUE_DATE")
    private LocalDate issueDate;

    @Column(name = "MAIN_FLAG", columnDefinition = " CHAR(1)")
    private String mainFlag;

    @Column(name = "FKGH_HAS_BEEN_ISSU")
    private String fkGhHasBeenIssu;

    @Column(name = "FKGH_HAS_TYPE")
    private String fkGhHasType;

    @Column(name = "ID_NO")
    private String idNo;

    @Column(name = "ISSUE_AUTHORITY", columnDefinition = "CHAR(30)")
    private String issueAuthority;

    @Column(name = "INCOMPLETE_U_COMNT", columnDefinition = "CHAR(30)")
    private String incompleteUComnt;
}