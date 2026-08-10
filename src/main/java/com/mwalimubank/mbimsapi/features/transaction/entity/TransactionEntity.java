package com.mwalimubank.mbimsapi.features.transaction.entity;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "transaction")
public class TransactionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private Integer unitId;

    @Column()
    private String userCode;

    @Column()
    private String customerId;

    @Column()
    private Date trxDate;

    @Column()
    private Integer trxSNum;

    @Column()
    private Integer trxSN;

    @Column()
    private Integer trxCode;

    @Column()
    private Integer productId;

    @Column()
    private Integer accountNumber;

    @Column()
    private Integer dcAmount;

    @Column()
    private Integer fcAmount;

    @Column()
    private Date availabilityDate;

    @Column()
    private Date tmstamp;

    @Column()
    private String currency;

    @Column()
    private String trxUserCode;

    @Column()
    private String glAccountCode;

    @Column()
    private String justification;

    @Column()
    private String subsystem;
}
