package com.mwalimubank.mbimsapi.features.administration.employee.entity;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.administration.position.PositionEntity;
import com.mwalimubank.mbimsapi.features.administration.unit.UnitEntity;
import com.mwalimubank.mbimsapi.features.user.UserEntity;
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

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

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

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "unit_id", nullable = true)
    private UnitEntity unit;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "department_id", nullable = true)
    private DepartmentEntity department;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "position_id", nullable = true)
    private PositionEntity position;
}
