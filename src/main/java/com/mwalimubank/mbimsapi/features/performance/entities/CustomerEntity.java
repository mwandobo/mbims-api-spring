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

    @Column(unique = true, nullable = false)
    private String first_name;

}
