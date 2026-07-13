package com.mwalimubank.mbimsapi.features.customer;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;   // better than String for dates
import java.time.LocalDateTime;

@Data
@Entity
@Table(schema = "profits", name = "customer")
public class CustomerEntity {

    @Id
    @Column(name = "cust_id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "surname", columnDefinition = "CHAR(70)")
    private String lastName;

    @Column(name = "sex", columnDefinition = "CHAR(1)")
    private String sex;

    @Column(name = "date_of_birth", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime dateOfBirth;// Use proper type


    // Contact details

    @Column(name = "MOBILE_TEL", length = 15)
    private String phoneNumber;

    @Column(name = "E_MAIL", columnDefinition = "CHAR(64)")
    private String email;


    // Personal details

    @Column(name = "CHILDREN_ABOVE18")
    private Short childrenAbove18;

    @Column(name = "NUM_OF_CHILDREN")
    private Short numberOfChildren;

    @Column(name = "FAMILY_MEMBERS")
    private Short familyMembers;

    @Column(name = "BIRTHPLACE", columnDefinition = "CHAR(20)")
    private String birthRegion;


    // Employment / profile

    @Column(name = "EMPLOYER", columnDefinition = "CHAR(40)")
    private String employer;

    @Column(name = "EMPLOYER_ADDRESS", columnDefinition = "CHAR(40)")
    private String employerAddress;


    // Identification related

    @Column(name = "DAI_NUMBER", columnDefinition = "CHAR(12)")
    private String identificationNumber;

    @Column(name = "FK_BISS_CODE")
    private Integer identificationType;


    // Additional flags

    @Column(name = "NON_RESIDENT", columnDefinition = "CHAR(1)")
    private String nonResident;

    @Column(name = "VIP_IND", columnDefinition = "CHAR(1)")
    private String vipIndicator;

    @Column(name = "BLACKLISTED_IND", columnDefinition = "CHAR(1)")
    private String blacklisted;


    // Dates

    @Column(name = "CUSTOMER_BEGIN_DAT", columnDefinition = "DATE")
    private String customerBeginDate;

    @Column(name = "CUST_OPEN_DATE", columnDefinition = "DATE")
    private String customerOpenDate;

    @Column(name = "title", columnDefinition = "CHAR(6)")
    private String title;


    @Column(name = "tmstamp", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime createdAt;             // Consider changing to OffsetDateTime / Timestamp

    @Column(name = "cust_type", columnDefinition = "CHAR(1)")
    private String custType;

    @Column(name = "cust_status", length = 1)
    private String custStatus;
}