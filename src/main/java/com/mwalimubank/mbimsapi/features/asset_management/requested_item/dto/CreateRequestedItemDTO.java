package com.mwalimubank.mbimsapi.features.asset_management.requested_item.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRequestedItemDTO {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;
}