package com.mwalimubank.mbimsapi.features.transaction.entity;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@Table(name = "transaction")
public class TransactionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_id")
    private Integer unitId;

    @Column(name = "user_code")
    private String userCode;

    @Column(name = "line_num")
    private Short lineNum;          // add this

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "trx_date")
    private LocalDate trxDate;      // prefer LocalDate over java.util.Date

    @Column(name = "trx_snum")
    private Long trxSNum;

    @Column(name = "trx_sn")
    private Integer trxSN;

    @Column(name = "trx_code")
    private Integer trxCode;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "account_number")
    private Long accountNumber;     // or BigDecimal if you want precision

    @Column(name = "dc_amount")
    private BigDecimal dcAmount;    // better than Integer

    @Column(name = "fc_amount")
    private Integer fcAmount;

    @Column(name = "availability_date")
    private Date availabilityDate;

    @Column(name = "tmstamp")
    private LocalDate tmstamp;

    @Column(name = "currency")
    private String currency;

    @Column(name = "trx_user_code")
    private String trxUserCode;

    @Column(name = "gl_account_code")
    private String glAccountCode;

    @Column(name = "justification")
    private String justification;

    @Column(name = "sub_system")
    private String subSystem;
}
