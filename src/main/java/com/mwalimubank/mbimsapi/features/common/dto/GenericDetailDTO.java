package com.mwalimubank.mbimsapi.features.common.dto;

import com.mwalimubank.mbimsapi.features.common.entity.GenericDetailEntity;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class GenericDetailDTO {
    private String id;
    private String description;
    private String shortDescription;
    private String parameterType;
    private String updatedAt;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static GenericDetailDTO fromEntity(GenericDetailEntity entity) {
        GenericDetailDTO dto = new GenericDetailDTO();
        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setShortDescription(entity.getShortDescription());
        dto.setParameterType(entity.getParameterType());
        return dto;
    }
}
