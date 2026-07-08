package com.mwalimubank.mbimsapi.features.customer;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;   // better than String for dates

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

    @Column(name = "title", columnDefinition = "CHAR(6)")
    private String title;

    @Column(name = "date_of_birth", columnDefinition = "DATE")
    private String dateOfBirth;        // Use proper type

    @Column(name = "tmstamp", columnDefinition = "TIMESTAMP(6)")
    private String createdAt;             // Consider changing to OffsetDateTime / Timestamp

    @Column(name = "cust_type", columnDefinition = "CHAR(1)")
    private String custType;

    @Column(name = "cust_status", length = 1)
    private String custStatus;
}