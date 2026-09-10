package com.mwalimubank.mbimsapi.features.administration.department;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.department.dto.CreateDepartmentDTO;
import com.mwalimubank.mbimsapi.features.administration.department.dto.DepartmentResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.administration.employee.dto.EmployeeResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.employee.entity.EmployeeEntity;
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
public class DepartmentService {
    private final DepartmentRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;
    private final PagedQueryService pagedQueryService;

    private static final Set<String> DEPARTMENT_SORT_FIELDS = Set.of(
            "id", "name", "description"
    );

    public PagedResponse<DepartmentResponseDTO > findAll(PaginationRequest pagination, String search) {
        Specification<DepartmentEntity> spec = PageSpecs.and(
                PageSpecs.notDeleted(),
                PageSpecs.searchLike(search, "name", "description")
        );

        return pagedQueryService.findAll(
                repository,
                spec,
                pagination,
                DepartmentEntity.class,
                DepartmentEntity::getId,
                DepartmentResponseDTO::fromEntity,
                DepartmentResponseDTO::setApprovalStatus,
                DEPARTMENT_SORT_FIELDS
        );
    }

    @Transactional
    public DepartmentResponseDTO create(CreateDepartmentDTO request) {
        repository.findByName(request.getName())
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Department with name '" + request.getName() + "' already exists"
                    );
                });

        DepartmentEntity entity = new DepartmentEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        DepartmentEntity savedEntity = repository.save(entity);

        return  DepartmentResponseDTO.fromEntity(savedEntity);
    }

    public  ApprovalAwareDTO<DepartmentResponseDTO> findOne  (Long  departmentId) {
          DepartmentEntity   department = repository.findById( departmentId)
                 .orElseThrow(() -> new IllegalStateException(" Department not found"));

          DepartmentResponseDTO dto = DepartmentResponseDTO.fromEntity(department);

           return approvalStatusUtil.attachApprovalInfo(
                    dto,
                    department.getId(),
                    DepartmentEntity.class.getSimpleName(),
                    currentUserService.getCurrentUserRoleId()
                );
     }

    @Transactional
    public DepartmentResponseDTO update(Long id, CreateDepartmentDTO request) {
        DepartmentEntity entity = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Department not found with id: " + id
                        )
                );

        repository.findByName(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Department with name '" + request.getName() + "' already exists"
                    );
                });

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());

        DepartmentEntity updatedEntity = repository.save(entity);

        return  DepartmentResponseDTO.fromEntity(updatedEntity);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        DepartmentEntity entity = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Department not found with id: " + id
                        )
                );

        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}
