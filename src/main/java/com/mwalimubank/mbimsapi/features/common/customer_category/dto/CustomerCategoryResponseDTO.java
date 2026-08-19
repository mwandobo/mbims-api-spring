package com.mwalimubank.mbimsapi.features.common.customer_category.dto;

import com.mwalimubank.mbimsapi.features.common.customer_category.CustomerCategoryEntity;
import lombok.Data;

@Data
public class CustomerCategoryResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String approvalStatus;
    private String createdAt;
    private String updatedAt;

    public static CustomerCategoryResponseDTO fromEntity(CustomerCategoryEntity entity) {
        CustomerCategoryResponseDTO dto = new CustomerCategoryResponseDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        return dto;
    }
}
