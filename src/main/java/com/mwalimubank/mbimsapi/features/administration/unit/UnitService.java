package com.mwalimubank.mbimsapi.features.administration.unit;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.administration.employee.entity.EmployeeEntity;
import com.mwalimubank.mbimsapi.features.administration.employee.repository.EmployeeRepository;
import com.mwalimubank.mbimsapi.features.administration.position.PositionEntity;
import com.mwalimubank.mbimsapi.features.administration.position.dto.PositionResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.unit.dto.CreateUnitDTO;
import com.mwalimubank.mbimsapi.features.administration.unit.dto.UnitResponseDTO;
import com.mwalimubank.mbimsapi.features.common.PageSpecs;
import com.mwalimubank.mbimsapi.features.common.services.PagedQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationDto;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UnitService {
    private final UnitRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;
    private final EmployeeRepository employeeRepository;
    private final PagedQueryService pagedQueryService;

    private static final Set<String> SORT_FIELDS = Set.of(
            "id", "name", "description", "code", "manager.name"
    );

    private static final Map<String, String> SORT_ALIASES = Map.of(
            "managerName", "manager.name"   // frontend sortBy=departmentName
    );


    public PagedResponse<UnitResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<UnitEntity> spec = PageSpecs.and(
                PageSpecs.notDeleted(),
                PageSpecs.searchLike(search, "name", "description", "code", "manager.name")
        );



        return pagedQueryService.findAll(
                repository,
                spec,
                pagination,
                UnitEntity.class,
                UnitEntity::getId,
                UnitResponseDTO::fromEntity,
                UnitResponseDTO::setApprovalStatus,
                SORT_FIELDS,
                SORT_ALIASES
        );
    }

    @Transactional
    public UnitResponseDTO create(CreateUnitDTO request) {
        UnitEntity entity = new UnitEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());


        // Department (optional)
        if (request.getManagerId() != null) {
            EmployeeEntity employee = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalStateException("Manager not found with id: " + request.getManagerId()));
            entity.setManager(employee);
        } else {
            entity.setManager(null);
        }

        entity.setCode(request.getCode());
        UnitEntity saved = repository.save(entity);
        return UnitResponseDTO.fromEntity(saved);
    }

    public ApprovalAwareDTO<UnitResponseDTO> findOne(Long id) {
        UnitEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Unit not found"));
        return approvalStatusUtil.attachApprovalInfo(
                UnitResponseDTO.fromEntity(entity),
                entity.getId(),
                UnitEntity.class.getSimpleName(),
                currentUserService.getCurrentUserRoleId()
        );
    }

    @Transactional
    public UnitResponseDTO update(Long id, CreateUnitDTO request) {
        UnitEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Unit not found"));

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());

        // Department (optional)
        if (request.getManagerId() != null) {
            EmployeeEntity employee = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalStateException("Manager not found with id: " + request.getManagerId()));
            entity.setManager(employee);
        } else {
            entity.setManager(null);
        }

        entity.setCode(request.getCode());

        UnitEntity updated = repository.save(entity);
        return UnitResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        UnitEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Unit not found"));
        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}
