package com.mwalimubank.mbimsapi.features.administration.position;


import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentRepository;
import com.mwalimubank.mbimsapi.features.administration.department.dto.DepartmentResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.position.dto.CreatePositionDTO;
import com.mwalimubank.mbimsapi.features.administration.position.dto.PositionResponseDTO;
import com.mwalimubank.mbimsapi.features.common.PageSpecs;
import com.mwalimubank.mbimsapi.features.common.services.PagedQueryService;
import com.mwalimubank.mbimsapi.features.role.RoleEntity;
import com.mwalimubank.mbimsapi.features.role.RoleRepository;
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
public class PositionService {
    private final PositionRepository repository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;

    private final PagedQueryService pagedQueryService;

    private static final Set<String> SORT_FIELDS = Set.of(
            "id", "name", "description","createdAt", "updatedAt",
            "department.name"           // real path
    );

    private static final Map<String, String> SORT_ALIASES = Map.of(
            "departmentName", "department.name"   // frontend sortBy=departmentName
    );

    public PagedResponse<PositionResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<PositionEntity> spec = PageSpecs.and(
                PageSpecs.notDeleted(),
                PageSpecs.searchLike(search, "name", "description","department.name" )
        );

        return pagedQueryService.findAll(
                repository,
                spec,
                pagination,
                PositionEntity.class,
                PositionEntity::getId,
                PositionResponseDTO::fromEntity,
                PositionResponseDTO::setApprovalStatus,
                SORT_FIELDS,
                SORT_ALIASES
        );
    }

    @Transactional
    public PositionResponseDTO create(CreatePositionDTO request) {

        DepartmentEntity department = validateDepartmentExists(request.getDepartmentId());

        // Check uniqueness: same name + same department
        repository.findByNameAndDepartmentId(request.getName(), request.getDepartmentId())
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Position with name '" + request.getName() +
                                    "' already exists in this department"
                    );
                });

        // Create Position
        PositionEntity entity = new PositionEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setDepartment(department);

        PositionEntity saved = repository.save(entity);

        // Create corresponding Role (simple – just use the position name)
        createRoleForPosition(saved);

        return PositionResponseDTO.fromEntity(saved);
    }

    private void createRoleForPosition(PositionEntity position) {
        // Only create if a role with this name does not already exist
        if (roleRepository.findByName(position.getName()).isEmpty()) {
            RoleEntity role = new RoleEntity();
            role.setName(position.getName());
            // set description or other fields if needed
            // role.setDescription("Role for position: " + position.getName());
            roleRepository.save(role);
        }
    }

    public ApprovalAwareDTO<PositionResponseDTO> findOne(Long id) {
        PositionEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Position not found"));
        return approvalStatusUtil.attachApprovalInfo(
                PositionResponseDTO.fromEntity(entity),
                entity.getId(),
                PositionEntity.class.getSimpleName(),
                currentUserService.getCurrentUserRoleId()
        );
    }

    @Transactional
    public PositionResponseDTO update(Long id, CreatePositionDTO request) {
        PositionEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Position not found"));

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        DepartmentEntity department = validateDepartmentExists(request.getDepartmentId());
        entity.setDepartment(department);

        PositionEntity updated = repository.save(entity);
        return PositionResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        PositionEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Position not found"));
        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }

    private DepartmentEntity validateDepartmentExists(Long id) {
        if (id == null) {
            if ("false" == "false") {
                throw new IllegalArgumentException("Department ID is required");
            }
            return null;
        }
        return departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Department not found with id: " + id));
    }

}
