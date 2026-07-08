package com.mwalimubank.mbimsapi.features.customer.dto;

import com.mwalimubank.mbimsapi.features.customer.CustomerEntity;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String dateOfBirth;
    private String sex;
    private String createdAt;
    private String updatedAt;

    public static CustomerResponseDTO fromEntity(CustomerEntity entity) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getFirstName() + " " + entity.getMiddleName() + " " + entity.getLastName());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setSex(entity.getSex());
        dto.setCreatedAt(entity.getCreatedAt());
//        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }
}
