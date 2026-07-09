package com.mwalimubank.mbimsapi.features.common.dto;

import com.mwalimubank.mbimsapi.features.common.entity.CustomerAddressEntity;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class CustomerAddressDTO {

    private Integer customerId;
    private Short serialNum;

    private String city;
    private String region;

    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String address5;
    private String address6;

    private String telephone;
    private String telephone2;
    private String telephone3;

    private String email;

    private String addressType;
    private String communicationAddress;

    private String accommodationDate;


    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public static CustomerAddressDTO fromEntity(CustomerAddressEntity entity) {

        CustomerAddressDTO dto = new CustomerAddressDTO();

        dto.setCustomerId(entity.getId().getFkCustomerCustId());
        dto.setSerialNum(entity.getId().getSerialNum());

        dto.setCity(entity.getCity());
        dto.setRegion(entity.getRegion());

        dto.setAddress1(entity.getAddress1());
        dto.setAddress2(entity.getAddress2());
        dto.setAddress3(entity.getAddress3());
        dto.setAddress4(entity.getAddress4());
        dto.setAddress5(entity.getAddress5());
        dto.setAddress6(entity.getAddress6());

        dto.setTelephone(entity.getTelephone());
        dto.setTelephone2(entity.getTelephone2());
        dto.setTelephone3(entity.getTelephone3());

        dto.setEmail(entity.getEmail());

        dto.setAddressType(entity.getAddressType());
        dto.setCommunicationAddress(entity.getCommunicationAddr());

        dto.setAccommodationDate(
                entity.getAccommodationDate() != null
                        ? entity.getAccommodationDate().format(FORMATTER)
                        : null
        );

        return dto;
    }
}