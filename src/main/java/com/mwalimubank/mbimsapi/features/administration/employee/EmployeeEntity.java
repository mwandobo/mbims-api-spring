package com.mwalimubank.mbimsapi.features.administration.employee;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(schema = "profits", name = "bankemployee")
public class EmployeeEntity   {

    @Id
    @Column(name = "ID", columnDefinition = "CHAR(8)")
    private String id;

    @Column(name = "FIRST_NAME", columnDefinition = "CHAR(40)")
    private String fatherName;

    @Column(name = "FATHER_NAME", columnDefinition = "VARCHAR(20)")
    private String firstName;

    @Column(name = "LAST_NAME", columnDefinition = "VARCHAR(20)")
    private String lastName;

    @Column(name = "SEX", columnDefinition = "VARCHAR(1)")
    private String sex;

    @Column(name = "EMAIL", columnDefinition = "VARCHAR(20)")
    private String email;

    @Column(name = "WORK_PHONE", columnDefinition = "VARCHAR(20)")
    private String workPhone;

    @Column(name = "MOBILE_PHONE", columnDefinition = "VARCHAR(20)")
    private String mobilePhone;

    @Column(name = "tmstamp", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime createdAt;
}
