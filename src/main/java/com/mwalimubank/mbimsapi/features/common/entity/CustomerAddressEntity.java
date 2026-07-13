package com.mwalimubank.mbimsapi.features.common.entity;

import com.mwalimubank.mbimsapi.features.common.entity.embedded_id.CustomerAddressId;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(schema = "profits", name = "cust_address")
public class CustomerAddressEntity {

    @EmbeddedId
    private CustomerAddressId id;

    @Column(name = "FKGD_HAS_COUNTRY")
    private Integer fkGdHasCountry;

    @Column(name = "FKGD_HAS_AS_DISTRI")
    private Integer fkGdHasAsDistri;

    @Column(name = "TMSTAMP", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime tmstamp;

    @Column(name = "COMMUNICATION_ADDR", columnDefinition = "CHAR(1)")
    private String communicationAddr;

    @Column(name = "PTS_IND", columnDefinition = "CHAR(1)")
    private String ptsInd;

    @Column(name = "ADDRESS_TYPE", columnDefinition = "CHAR(1)")
    private String addressType;

    @Column(name = "ENTRY_STATUS", columnDefinition = "CHAR(1)")
    private String entryStatus;

    @Column(name = "LATIN_IND", columnDefinition = "CHAR(1)")
    private String latinInd;

    @Column(name = "FKGH_HAS_COUNTRY", columnDefinition = "CHAR(5)")
    private String fkGhHasCountry;

    @Column(name = "SEGM_FLAGS", columnDefinition = "CHAR(5)")
    private String segmFlags;

    @Column(name = "FKGH_HAS_AS_DISTRI", columnDefinition = "CHAR(5)")
    private String fkGhHasAsDistri;

    @Column(name = "MAIL_BOX", columnDefinition = "CHAR(5)")
    private String mailBox;

    @Column(name = "ZIP_CODE", columnDefinition = "CHAR(10)")
    private String zipCode;

    @Column(name = "FAX_NO", columnDefinition = "CHAR(15)")
    private String faxNo;

    @Column(name = "TELEPHONE", columnDefinition = "CHAR(15)")
    private String telephone;

    @Column(name = "CITY", columnDefinition = "CHAR(30)")
    private String city;

    @Column(name = "REGION", columnDefinition = "VARCHAR(20)")
    private String region;

    @Column(name = "ADDRESS_1", columnDefinition = "VARCHAR(40)")
    private String address1;

    @Column(name = "ADDRESS_2", columnDefinition = "VARCHAR(40)")
    private String address2;

    @Column(name = "ADDRESS_3", columnDefinition = "VARCHAR(40)")
    private String address3;

    @Column(name = "ADDRESS_4", columnDefinition = "VARCHAR(40)")
    private String address4;

    @Column(name = "ADDRESS_5", columnDefinition = "VARCHAR(40)")
    private String address5;

    @Column(name = "ADDRESS_6", columnDefinition = "VARCHAR(40)")
    private String address6;

    @Column(name = "ENTRY_COMMENTS", columnDefinition = "VARCHAR(250)")
    private String entryComments;

    @Column(name = "FK_CUST_ADDR_PACO", columnDefinition = "CHAR(8)")
    private String fkCustAddrPaco;

    @Column(name = "FK_CUST_ADDR_PASN")
    private Integer fkCustAddrPasn;

    @Column(name = "ACCOMODATION_DATE", columnDefinition = "DATE")
    private LocalDate accommodationDate;

    @Column(name = "INTERNET_ADDRESS", columnDefinition = "VARCHAR(100)")
    private String internetAddress;

    @Column(name = "E_MAIL", columnDefinition = "VARCHAR(60)")
    private String email;

    @Column(name = "TELEPHONE_2", columnDefinition = "VARCHAR(15)")
    private String telephone2;

    @Column(name = "TELEPHONE_3", columnDefinition = "VARCHAR(15)")
    private String telephone3;
}