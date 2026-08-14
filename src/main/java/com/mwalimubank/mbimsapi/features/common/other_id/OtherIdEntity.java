package com.mwalimubank.mbimsapi.features.common.other_id;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "other_id")
public class OtherIdEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "serial_no", nullable = false)
    private Short serialNo;

    @Column(name = "id_type_id")
    private Integer idTypeId;                     // FKGD_HAS_TYPE

    @Column(name = "issued_by_id")
    private Integer issuedById;                   // FKGD_HAS_BEEN_ISSU

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "tmstamp")
    private LocalDateTime tmstamp;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "main_flag", length = 1)
    private String mainFlag;

    @Column(name = "issued_by_code", length = 20)
    private String issuedByCode;                  // FKGH_HAS_BEEN_ISSU

    @Column(name = "id_type_code", length = 20)
    private String idTypeCode;                    // FKGH_HAS_TYPE

    @Column(name = "id_no", length = 50)
    private String idNo;

    @Column(name = "issue_authority", length = 30)
    private String issueAuthority;

    @Column(name = "incomplete_comment", length = 30)
    private String incompleteComment;             // INCOMPLETE_U_COMNT
}
