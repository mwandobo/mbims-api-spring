package com.mwalimubank.mbimsapi.features.performance.entities;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(schema = "profits", name = "customer")
public class CustomerEntity  {
    @Id
    private Long cust_id;

    @Column()
    private String first_name;

    @Column()
    private String last_name;

    @Column(name = "CUST_TYPE")
    private String custType;

    @Column(name = "CUST_STATUS")
    private String custStatus;

}
