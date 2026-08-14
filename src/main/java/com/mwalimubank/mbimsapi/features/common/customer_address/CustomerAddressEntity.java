package com.mwalimubank.mbimsapi.features.common.customer_address;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "customer_address")
public class CustomerAddressEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "serial_num", nullable = false)
    private Short serialNum;

    @Column(name = "country_id")
    private Integer countryId;                    // FKGD_HAS_COUNTRY

    @Column(name = "district_id")
    private Integer districtId;                   // FKGD_HAS_AS_DISTRI

    @Column(name = "tmstamp")
    private LocalDateTime tmstamp;

    @Column(name = "communication_addr", length = 1)
    private String communicationAddr;

    @Column(name = "pts_ind", length = 1)
    private String ptsInd;

    @Column(name = "address_type", length = 1)
    private String addressType;

    @Column(name = "entry_status", length = 1)
    private String entryStatus;

    @Column(name = "latin_ind", length = 1)
    private String latinInd;

    @Column(name = "country_code", length = 5)
    private String countryCode;                   // FKGH_HAS_COUNTRY

    @Column(name = "segm_flags", length = 5)
    private String segmFlags;

    @Column(name = "district_code", length = 5)
    private String districtCode;                  // FKGH_HAS_AS_DISTRI

    @Column(name = "mail_box", length = 5)
    private String mailBox;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "fax_no", length = 15)
    private String faxNo;

    @Column(name = "telephone", length = 15)
    private String telephone;

    @Column(name = "city", length = 30)
    private String city;

    @Column(name = "region", length = 20)
    private String region;

    @Column(name = "address_1", length = 40)
    private String address1;

    @Column(name = "address_2", length = 40)
    private String address2;

    @Column(name = "address_3", length = 40)
    private String address3;

    @Column(name = "address_4", length = 40)
    private String address4;

    @Column(name = "address_5", length = 40)
    private String address5;

    @Column(name = "address_6", length = 40)
    private String address6;

    @Column(name = "entry_comments", length = 250)
    private String entryComments;

    @Column(name = "paco", length = 8)
    private String paco;                          // FK_CUST_ADDR_PACO

    @Column(name = "pasn")
    private Integer pasn;                         // FK_CUST_ADDR_PASN

    @Column(name = "accommodation_date")
    private LocalDate accommodationDate;

    @Column(name = "internet_address", length = 100)
    private String internetAddress;

    @Column(name = "email", length = 60)
    private String email;

    @Column(name = "telephone_2", length = 15)
    private String telephone2;

    @Column(name = "telephone_3", length = 15)
    private String telephone3;
}
