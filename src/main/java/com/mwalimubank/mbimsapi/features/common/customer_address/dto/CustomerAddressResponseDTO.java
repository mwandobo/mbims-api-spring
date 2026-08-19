package com.mwalimubank.mbimsapi.features.common.customer_address.dto;

import com.mwalimubank.mbimsapi.features.common.customer_address.CustomerAddressEntity;
import lombok.Data;

@Data
public class CustomerAddressResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String approvalStatus;
    private String createdAt;
    private String updatedAt;

    public static CustomerAddressResponseDTO fromEntity(CustomerAddressEntity entity) {
        CustomerAddressResponseDTO dto = new CustomerAddressResponseDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }
}
