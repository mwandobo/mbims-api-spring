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

    @Column(name = "last_name")
    private String lastName;

    @Column()
    private String name;

    @Column()
    private String sex;

    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;// Use proper type

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column()
    private String email;

    @Column(name = "children_above_18")
    private Short childrenAbove18;

    @Column(name = "number_of_children")
    private Short numberOfChildren;

    @Column(name = "family_members")
    private Short familyMembers;

    @Column(name = "birth_region")
    private String birthRegion;

    @Column()
    private String employer;

    @Column(name = "employer_address")
    private String employerAddress;

    @Column(name = "identification_number")
    private String identificationNumber;

    @Column(name = "identification_type")
    private Integer identificationType;

    @Column(name = "non_resident")
    private String nonResident;

    @Column(name = "vip_indicator")
    private String vipIndicator;

    @Column(name = "blacklist_indicator")
    private String blacklisted;

    @Column(name = "customer_begin_date")
    private String customerBeginDate;

    @Column(name = "customer_open_date")
    private String customerOpenDate;

    @Column()
    private String title;

    @Column(name = "employee_code")
    private String employeeCode;

    @Column(name = "cust_type")
    private String custType;

    @Column(name = "cust_status")
    private String custStatus;
}
