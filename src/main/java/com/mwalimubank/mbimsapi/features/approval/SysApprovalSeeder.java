package com.mwalimubank.mbimsapi.features.approval;

import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.approval.dto.SysApprovalRequestDTO;
import com.mwalimubank.mbimsapi.features.approval.entity.SysApprovalEntity;
import com.mwalimubank.mbimsapi.features.approval.enums.StatusEnum;
import com.mwalimubank.mbimsapi.features.approval.repository.SysApprovalRepository;
import com.mwalimubank.mbimsapi.features.asset_management.asset_request.AssetRequestEntity;
import com.mwalimubank.mbimsapi.features.role.RoleEntity;
import com.mwalimubank.mbimsapi.features.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SysApprovalSeeder implements CommandLineRunner {

    private final SysApprovalRepository repository;

    @Override
    public void run(String... args) {
        seedApprovals();
    }

    private void seedApprovals() {
        List<SysApprovalRequestDTO> approvals = List.of(
                createDto("User Approval", "Approvals For User", UserEntity.class.getSimpleName(), StatusEnum.PENDING),
                createDto("Role Approval", "Approvals For  for Role", RoleEntity.class.getSimpleName(), StatusEnum.PENDING),
                createDto("Department Approval", "Approvals For  for Department", DepartmentEntity.class.getSimpleName(), StatusEnum.PENDING),
                createDto("Asset Request Approval", "Approvals For Asset Request", AssetRequestEntity.class.getSimpleName(), StatusEnum.PENDING)
        );

        for (SysApprovalRequestDTO dto : approvals) {
            if (repository.findByName(dto.getName()) == null) {
                SysApprovalEntity entity = new SysApprovalEntity();
                entity.setName(dto.getName());
                entity.setDescription(dto.getDescription());
                entity.setEntityName(dto.getEntityName());
                entity.setStatus(dto.getStatus());
                repository.save(entity);
                System.out.println("Seeded approval: " + dto.getName());
            } else {
                System.out.println("Approval already exists: " + dto.getName());
            }
        }
    }

    private SysApprovalRequestDTO createDto(String name, String description, String entityName, StatusEnum status) {
        SysApprovalRequestDTO dto = new SysApprovalRequestDTO();
        dto.setName(name);
        dto.setDescription(description);
        dto.setEntityName(entityName);
        dto.setStatus(status);
        return dto;
    }
}
