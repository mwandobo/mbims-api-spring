package com.mwalimubank.mbimsapi.features.customer.dto;

import com.mwalimubank.mbimsapi.features.customer.CustomerEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String dateOfBirth;
    private String customerType;
    private String sex;
    private String createdAt;
    private String updatedAt;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static CustomerResponseDTO fromEntity(CustomerEntity entity) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getFirstName() + " " + entity.getMiddleName() + " " + entity.getLastName());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setSex(
                switch (entity.getSex()) {
                    case "M" -> "Male";
                    case "F" -> "Female";
                    default -> "Not Applicable";
                }
        );
        dto.setSex(
                switch (entity.getSex()) {
                    case "M" -> "Male";
                    case "F" -> "Female";
                    default -> "Not Applicable";
                }
        );
        dto.setCreatedAt(
                entity.getCreatedAt() != null
                        ? entity.getCreatedAt().format(FORMATTER)
                        : null
        );
        dto.setCustomerType(Objects.equals(entity.getCustType(), "1") ? "Individual": "Corporate");
//        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }
}
