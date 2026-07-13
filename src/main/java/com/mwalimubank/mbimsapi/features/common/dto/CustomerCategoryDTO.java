package com.mwalimubank.mbimsapi.features.common.dto;

import com.mwalimubank.mbimsapi.features.common.entity.CustomerCategoryEntity;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class CustomerCategoryDTO {

    private Integer customerId;

    private String category;

    private Integer genericDetailSerial;

    private String genericDetailKey;

    private String createdAt;


    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public static CustomerCategoryDTO fromEntity(CustomerCategoryEntity entity) {

        CustomerCategoryDTO dto = new CustomerCategoryDTO();

        dto.setCustomerId(
                entity.getId().getFkCustomerCustId()
        );

        dto.setCategory(
                entity.getId().getCategory()
        );

        dto.setGenericDetailSerial(
                entity.getFkGenericDetaSer()
        );

        dto.setGenericDetailKey(
                entity.getGenericDetail()
        );

        dto.setCreatedAt(
                entity.getTmstamp() != null
                        ? entity.getTmstamp().format(FORMATTER)
                        : null
        );

        return dto;
    }
}