package com.mwalimubank.mbimsapi.features.role.dto;

import com.mwalimubank.mbimsapi.features.permission.PermissionEntity;
import com.mwalimubank.mbimsapi.features.permission.dto.PermissionResponseDTO;
import com.mwalimubank.mbimsapi.features.role.RoleEntity;
import lombok.Data;

import java.util.List;

@Data
public class RoleResponseDTO {
    private Long id;
    private String name;
    private String approvalStatus;
    private List<PermissionResponseDTO> permissions;

    public static RoleResponseDTO fromEntity(RoleEntity role) {
        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setPermissions(
                role.getPermissions()
                        .stream()
                        .map(PermissionResponseDTO::fromEntity)
                        .toList()
        );
        return dto;
    }
}