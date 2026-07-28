package com.mwalimubank.mbimsapi.features.administration.employee.entity;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "employee")
public class EmployeeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String name;

    @Column()
    private String gender;

    @Column()
    private String email;

    @Column()
    private String staffNo;

    @Column()
    private String workPhone;

    @Column()
    private String mobilePhone;

}
