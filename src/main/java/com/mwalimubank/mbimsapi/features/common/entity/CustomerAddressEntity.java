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


    @Column(name = "TMSTAMP")
    private LocalDateTime tmstamp;


    @Column(name = "COMMUNICATION_ADDR")
    private String communicationAddr;

    @Column(name = "PTS_IND")
    private String ptsInd;

    @Column(name = "ADDRESS_TYPE")
    private String addressType;

    @Column(name = "ENTRY_STATUS")
    private String entryStatus;

    @Column(name = "LATIN_IND")
    private String latinInd;


    @Column(name = "FKGH_HAS_COUNTRY")
    private String fkGhHasCountry;

    @Column(name = "SEGM_FLAGS")
    private String segmFlags;

    @Column(name = "FKGH_HAS_AS_DISTRI")
    private String fkGhHasAsDistri;


    @Column(name = "MAIL_BOX")
    private String mailBox;

    @Column(name = "ZIP_CODE")
    private String zipCode;

    @Column(name = "FAX_NO")
    private String faxNo;

    @Column(name = "TELEPHONE")
    private String telephone;


    @Column(name = "CITY")
    private String city;

    @Column(name = "REGION")
    private String region;


    @Column(name = "ADDRESS_1")
    private String address1;

    @Column(name = "ADDRESS_2")
    private String address2;

    @Column(name = "ADDRESS_3")
    private String address3;

    @Column(name = "ADDRESS_4")
    private String address4;

    @Column(name = "ADDRESS_5")
    private String address5;

    @Column(name = "ADDRESS_6")
    private String address6;


    @Column(name = "ENTRY_COMMENTS")
    private String entryComments;


    @Column(name = "FK_CUST_ADDR_PACO")
    private String fkCustAddrPaco;

    @Column(name = "FK_CUST_ADDR_PASN")
    private Integer fkCustAddrPasn;


    @Column(name = "ACCOMODATION_DATE")
    private LocalDate accommodationDate;


    @Column(name = "INTERNET_ADDRESS")
    private String internetAddress;

    @Column(name = "E_MAIL")
    private String email;


    @Column(name = "TELEPHONE_2")
    private String telephone2;

    @Column(name = "TELEPHONE_3")
    private String telephone3;
}