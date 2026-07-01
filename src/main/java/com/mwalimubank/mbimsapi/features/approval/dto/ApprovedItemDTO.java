package com.mwalimubank.mbimsapi.features.approval.dto;

import lombok.Data;

@Data
public class ApprovedItemDTO {
    private Asset asset;

    @Data
    public static class Asset {
        private String name;
    }
}
