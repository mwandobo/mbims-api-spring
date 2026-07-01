package com.mwalimubank.mbimsapi.features.asset_management.asset.dto;

import lombok.Data;

@Data
public class CreateAssetDTO {
    private String name;
    private String description;
    private Long asset_category_id;
}
