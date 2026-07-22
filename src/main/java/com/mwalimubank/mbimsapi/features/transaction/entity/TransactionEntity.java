package com.mwalimubank.mbimsapi.features.transaction.entity;

import com.mwalimubank.mbimsapi.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(schema = "profits", name = "gli_trx_extract")
public class TransactionEntity  {

//    @EmbeddedId
//    private TransactionId id;

    @Id
    @Column(name = "TRN_SNUM")
    private Long id;

    @Column(name = "FK_UNITCODETRXUNIT", nullable = false)
    private Integer fkUnitcodetrxunit;

    @Column(name = "FK_USRCODE", nullable = false, length = 8)
    private String fkUsrCode;

    @Column(name = "LINE_NUM", nullable = false)
    private Short lineNum;

    @Column(name = "TRN_DATE", nullable = false, columnDefinition = "DATE")
    private LocalDate trnDate;

    @Column(name = "ACC_C_DIGIT")
    private Short accCDigit;

    @Column(name = "AMOUNT_SER_NO")
    private Short amountSerNo;

    @Column(name = "TUN_INTERNAL_SN")
    private Short tunInternalSn;

    @Column(name = "AVAILABILITY_DAYS")
    private Short availabilityDays;

    @Column(name = "CUST_ID")
    private Integer custId;

//    @Column(name = "GL_RULE")
//    private Integer glRule;
//
//    @Column(name = "TRX_CODE")
//    private Integer trxCode;
//
//    @Column(name = "ID_PRODUCT")
//    private Integer idProduct;
//
//    @Column(name = "FK1UNITCODE")
//    private Integer fk1UnitCode;
//
//    @Column(name = "FK0UNITCODE")
//    private Integer fk0UnitCode;
//

//
//    @Column(name = "TRX_SN")
//    private Integer trxSn;
//
//    @Column(name = "BILL_SERIAL_NUM", precision = 10)
//    private BigDecimal billSerialNum;
//
//    @Column(name = "ACCOUNT_NUMBER", precision = 13)
//    private BigDecimal accountNumber;
//
//    @Column(name = "FC_AMOUNT", precision = 15, scale = 2)
//    private BigDecimal fcAmount;
//
//    @Column(name = "DC_AMOUNT", precision = 15, scale = 2)
//    private BigDecimal dcAmount;
//
//    @Column(name = "AVAILABILITY_DATE", columnDefinition = "DATE")
//    private LocalDate availabilityDate;
//
//    @Column(name = "TRX_GL_TRN_DATE", columnDefinition = "DATE")
//    private LocalDate trxGlTrnDate;
//
//    @Column(name = "TMSTAMP", columnDefinition = "TIMESTAMP(6)")
//    private LocalDate tmstamp;
//
//    @Column(name = "ENTRY_TYPE", columnDefinition = "CHAR(1)")
//    private String entryType;
//
//    @Column(name = "MIGRATED_FLAG", columnDefinition = "CHAR(1)")
//    private String migratedFlag;
//
//    @Column(name = "LEVEL0", columnDefinition = "CHAR(1)")
//    private String level0;
//
//    @Column(name = "THIRDPARTY_IND", columnDefinition = "CHAR(1)")
//    private String thirdpartyInd;
//
//    @Column(name = "SUBSYSTEM", columnDefinition = "CHAR(2)")
//    private String subsystem;
//
//    @Column(name = "ACH_BANK_CODE", columnDefinition = "CHAR(2)")
//    private String achBankCode;
//
//    @Column(name = "GLACC_ORIGIN", columnDefinition = "CHAR(2)")
//    private String glaccOrigin;
//
//    @Column(name = "ID_JUSTIFIC", columnDefinition = "CHAR(5)")
//    private String idJustific;
//
//    @Column(name = "CURRENCY_SHORT_DES", columnDefinition = "CHAR(5)")
//    private String currencyShortDes;
//
//    @Column(name = "FIELD_1_2_SEPERAT", columnDefinition = "CHAR(6)")
//    private String field12Seperat;
//
//    @Column(name = "FIELD_3_4_SEPERAT", columnDefinition = "CHAR(6)")
//    private String field34Seperat;
//
//    @Column(name = "TRX_USR", columnDefinition = "CHAR(8)")
//    private String trxUsr;
//
//    @Column(name = "CHEQUE_NUMBER", columnDefinition = "CHAR(10)")
//    private String chequeNumber;
//
//    @Column(name = "CUSTOMER_ID_NO", columnDefinition = "CHAR(20)")
//    private String customerIdNo;
//
//    @Column(name = "BILL_NUMBER", columnDefinition = "CHAR(20)")
//    private String billNumber;
//
//    @Column(name = "FK_GLG_ACCOUNTACCO", columnDefinition = "CHAR(21)")
//    private String fkGlgAccountacco;
//
//    @Column(name = "TPP_FIELD_3", columnDefinition = "CHAR(30)")
//    private String tppField3;
//
//    @Column(name = "TPP_FIELD_2", columnDefinition = "CHAR(30)")
//    private String tppField2;
//
//    @Column(name = "TPP_FIELD_1", columnDefinition = "CHAR(30)")
//    private String tppField1;
//
//    @Column(name = "TPP_FIELD_4", columnDefinition = "CHAR(30)")
//    private String tppField4;
//
//    @Column(name = "ENTRY_COMMENTS", columnDefinition = "CHAR(40)")
//    private String entryComments;
//
//    @Column(name = "EXTERNAL_GLACCOUNT", columnDefinition = "CHAR(21)")
//    private String externalGlAccount;
//
//    @Column(name = "JUSTIFIC_DESCR", columnDefinition = "VARCHAR(40)")
//    private String justificDescr;
//
//    @Column(name = "PRF_ACCOUNT_NUMBER", columnDefinition = "CHAR(40)")
//    private String prfAccountNumber;
//
//    @Column(name = "PRF_ACC_CD")
//    private Short prfAccCd;

}
