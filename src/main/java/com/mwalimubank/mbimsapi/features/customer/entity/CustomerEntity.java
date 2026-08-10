package com.mwalimubank.mbimsapi.features.customer.entity;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "customer")
public class CustomerEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cust_id")
    private Long custId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "surname")
    private String lastName;

    @Column(name = "name")
    private String name;

    @Column(name = "sex")
    private String sex;

    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;// Use proper type

    @Column(name = "MOBILE_TEL")
    private String phoneNumber;

    @Column(name = "E_MAIL")
    private String email;

    @Column(name = "CHILDREN_ABOVE18")
    private Short childrenAbove18;

    @Column(name = "NUM_OF_CHILDREN")
    private Short numberOfChildren;

    @Column(name = "FAMILY_MEMBERS")
    private Short familyMembers;

    @Column(name = "BIRTHPLACE")
    private String birthRegion;

    @Column(name = "EMPLOYER")
    private String employer;

    @Column(name = "EMPLOYER_ADDRESS")
    private String employerAddress;

    @Column(name = "DAI_NUMBER")
    private String identificationNumber;

    @Column(name = "FK_BISS_CODE")
    private Integer identificationType;

    @Column(name = "NON_RESIDENT")
    private String nonResident;

    @Column(name = "VIP_IND")
    private String vipIndicator;

    @Column(name = "BLACKLISTED_IND")
    private String blacklisted;

    @Column(name = "CUSTOMER_BEGIN_DAT")
    private String customerBeginDate;

    @Column(name = "CUST_OPEN_DATE")
    private String customerOpenDate;

    @Column(name = "title")
    private String title;

    @Column(name = "cust_type")
    private String custType;

    @Column(name = "cust_status")
    private String custStatus;
}
