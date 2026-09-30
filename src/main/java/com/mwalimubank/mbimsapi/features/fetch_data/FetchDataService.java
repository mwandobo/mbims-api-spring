package com.mwalimubank.mbimsapi.features.fetch_data;

import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentRepository;
import com.mwalimubank.mbimsapi.features.administration.department.dto.DepartmentResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.employee.entity.EmployeeEntity;
import com.mwalimubank.mbimsapi.features.administration.employee.repository.EmployeeRepository;
import com.mwalimubank.mbimsapi.features.administration.employee.dto.EmployeeResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.position.PositionEntity;
import com.mwalimubank.mbimsapi.features.administration.position.PositionRepository;
import com.mwalimubank.mbimsapi.features.administration.position.dto.PositionResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.unit.UnitEntity;
import com.mwalimubank.mbimsapi.features.administration.unit.UnitRepository;
import com.mwalimubank.mbimsapi.features.administration.unit.dto.UnitResponseDTO;
import com.mwalimubank.mbimsapi.features.approval.dto.SysApprovalResponseDTO;
import com.mwalimubank.mbimsapi.features.approval.entity.SysApprovalEntity;
import com.mwalimubank.mbimsapi.features.approval.repository.SysApprovalRepository;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.features.asset_management.asset.AssetEntity;
import com.mwalimubank.mbimsapi.features.asset_management.asset.AssetRepository;
import com.mwalimubank.mbimsapi.features.asset_management.asset.dto.AssetResponseDTO;
import com.mwalimubank.mbimsapi.features.asset_management.assetcategory.AssetCategoryEntity;
import com.mwalimubank.mbimsapi.features.asset_management.assetcategory.AssetCategoryRepository;
import com.mwalimubank.mbimsapi.features.asset_management.assetcategory.dto.AssetCategoryResponseDTO;
import com.mwalimubank.mbimsapi.features.role.RoleEntity;
import com.mwalimubank.mbimsapi.features.role.RoleRepository;
import com.mwalimubank.mbimsapi.features.role.dto.RoleResponseDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Data
@Service
@RequiredArgsConstructor
public class FetchDataService {
    private final ApprovalStatusUtil approvalStatusUtil;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final SysApprovalRepository sysApprovalRepository;
    private final AssetCategoryRepository assetCategoryRepository;
    private final EmployeeRepository employeeRepository;
    private final UnitRepository unitRepository;
    private final PositionRepository positionRepository;
    private final AssetRepository assetRepository;


    private <E, D> List<D> fetchData(
            List<E> entities,
            String entityName,
            Function<E, Long> idExtractor,
            Function<E, D> dtoMapper,
            BiConsumer<D, String> approvalSetter
    ) {
        boolean hasApprovalMode = approvalStatusUtil.hasApprovalMode(entityName);

        List<Long> ids = entities.stream()
                .map(idExtractor)
                .toList();

        Map<Long, String> statusMap = hasApprovalMode
                ? approvalStatusUtil.getBulkApprovalStatuses(entityName, ids)
                : Collections.emptyMap();

        return entities.stream()
                .map(entity -> {
                    D dto = dtoMapper.apply(entity);

                    if (hasApprovalMode) {
                        approvalSetter.accept(
                                dto,
                                statusMap.get(idExtractor.apply(entity))
                        );
                    }

                    return dto;
                })
                .toList();
    }


    public List<DepartmentResponseDTO> fetchDepartments() {

        return fetchData(
                departmentRepository.findAll(),
                DepartmentEntity.class.getSimpleName(),
                DepartmentEntity::getId,
                DepartmentResponseDTO::fromEntity,
                DepartmentResponseDTO::setApprovalStatus
        );
    }


    public List<RoleResponseDTO> fetchRoles() {

        return fetchData(
                roleRepository.findAll(),
                RoleEntity.class.getSimpleName(),
                RoleEntity::getId,
                RoleResponseDTO::fromEntity,
                RoleResponseDTO::setApprovalStatus
        );
    }

    public List<SysApprovalResponseDTO> fetchSysApprovals() {

        return fetchData(
                sysApprovalRepository.findAll(),
                SysApprovalEntity.class.getSimpleName(),
                SysApprovalEntity::getId,
                SysApprovalResponseDTO::fromEntity,
                SysApprovalResponseDTO::setApprovalStatus
        );
    }

    public List<AssetCategoryResponseDTO> fetchAssetCategories() {

        return fetchData(
                assetCategoryRepository.findAll(),
                AssetCategoryEntity.class.getSimpleName(),
                AssetCategoryEntity::getId,
                AssetCategoryResponseDTO::fromEntity,
                AssetCategoryResponseDTO::setApprovalStatus
        );
    }

    public List<EmployeeResponseDTO> fetchEmployees() {

        return fetchData(
                employeeRepository.findAll(),
                EmployeeEntity.class.getSimpleName(),
                EmployeeEntity::getId,
                EmployeeResponseDTO::fromEntity,
                EmployeeResponseDTO::setApprovalStatus
        );
    }

    public List<UnitResponseDTO> fetchUnits() {
        return fetchData(
                unitRepository.findAll(),
                UnitEntity.class.getSimpleName(),
                UnitEntity::getId,
                UnitResponseDTO::fromEntity,
                UnitResponseDTO::setApprovalStatus
        );
    }

    public List<PositionResponseDTO> fetchPositions() {
        return fetchData(
                positionRepository.findAll(),
                PositionEntity.class.getSimpleName(),
                PositionEntity::getId,
                PositionResponseDTO::fromEntity,
                PositionResponseDTO::setApprovalStatus
        );
    }

    public List<AssetResponseDTO> fetchAssetsByAssetCategories(Long assetCategoryId) {

        List<AssetEntity> assets;

        if (assetCategoryId != null) {
            assets = assetRepository.findByAssetCategoryId(assetCategoryId);
        } else {
            assets = assetRepository.findAll();
        }

        return fetchData(
                assets,
                AssetEntity.class.getSimpleName(),
                AssetEntity::getId,
                AssetResponseDTO::fromEntity,
                AssetResponseDTO::setApprovalStatus
        );
    }

}
