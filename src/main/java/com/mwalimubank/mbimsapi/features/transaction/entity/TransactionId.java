package com.mwalimubank.mbimsapi.features.transaction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TransactionId implements Serializable {

    @Column(name = "FK_UNITCODETRXUNIT", nullable = false)
    private Integer fkUnitcodetrxunit;

    @Column(name = "FK_USRCODE", nullable = false, length = 8)
    private String fkUsrCode;

    @Column(name = "LINE_NUM", nullable = false)
    private Short lineNum;

    @Column(name = "TRN_DATE", nullable = false, columnDefinition = "DATE")
    private LocalDate trnDate;

    @Column(name = "TRN_SNUM", nullable = false)
    private Integer trnSnum;
}