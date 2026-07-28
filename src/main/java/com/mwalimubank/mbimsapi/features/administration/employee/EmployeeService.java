package com.mwalimubank.mbimsapi.features.administration.employee;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.employee.dto.EmployeeResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.employee.entity.EmployeeEntity;
import com.mwalimubank.mbimsapi.features.administration.employee.repository.EmployeeRepository;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationDto;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;

    public PagedResponse<EmployeeResponseDTO> findAll(
            PaginationRequest pagination,
            String search
    ) {
        Specification<EmployeeEntity> spec = getEntitySpecification(search);
        boolean hasApprovalMode = approvalStatusUtil.hasApprovalMode(EmployeeEntity.class.getSimpleName());

        Page<EmployeeEntity> page =
                repository.findAll(spec, pagination.toPageable());

        List<EmployeeEntity> entities = page.getContent();

        List<Long> ids = entities.stream()
                .map(EmployeeEntity::getId)
                .toList();
        Map<Long, String> statusMap = hasApprovalMode
                ? approvalStatusUtil.getBulkApprovalStatuses(EmployeeEntity.class.getSimpleName(), ids)
                : Collections.emptyMap();

        List<EmployeeResponseDTO> result = entities.stream()
                .map(entity -> {
                    EmployeeResponseDTO dto = EmployeeResponseDTO.fromEntity(entity);

                    if (hasApprovalMode) {
                        dto.setApprovalStatus(
                                statusMap.get(entity.getId())
                        );
                    }

                    return dto;
                })
                .toList();

        return new PagedResponse<>(
                result,
                new PaginationDto(
                        page.getTotalElements(),
                        page.getNumber() + 1,
                        page.getSize(),
                        page.getTotalPages()
                ),
                hasApprovalMode // or dynamic logic
        );
    }

    private static Specification< EmployeeEntity> getEntitySpecification(String search) {
        Specification< EmployeeEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));

        // Optional search filter (case-insensitive)
        if (search != null && !search.trim().isEmpty()) {
            String likePattern = "%" + search.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("title")), likePattern),
                            cb.like(cb.lower(root.get("description")), likePattern)
                    )
            );
        }
        return spec;
    }

//    @Transactional
//    public EmployeeResponseDTO create(CreateEmployeeDTO request) {
//        repository.findByName(request.getName())
//                .ifPresent(existing -> {
//                    throw new IllegalStateException(
//                            "Employee with name '" + request.getName() + "' already exists"
//                    );
//                });
//
//        EmployeeEntity entity = new EmployeeEntity();
//        entity.setName(request.getName());
//        entity.setDescription(request.getDescription());
//        EmployeeEntity savedEntity = repository.save(entity);
//
//        return  EmployeeResponseDTO.fromEntity(savedEntity);
//    }

    public ApprovalAwareDTO<EmployeeResponseDTO> findOne  (Long  departmentId) {
        EmployeeEntity   department = repository.findById( departmentId)
                .orElseThrow(() -> new IllegalStateException(" Employee not found"));

        EmployeeResponseDTO dto = EmployeeResponseDTO.fromEntity(department);

        return approvalStatusUtil.attachApprovalInfo(
                dto,
                department.getId(),
                EmployeeEntity.class.getSimpleName(),
                currentUserService.getCurrentUserRoleId()
        );
    }

//    @Transactional
//    public EmployeeResponseDTO update(Long id, CreateEmployeeDTO request) {
//        EmployeeEntity entity = repository.findById(id)
//                .orElseThrow(() ->
//                        new IllegalStateException(
//                                "Employee not found with id: " + id
//                        )
//                );
//
//        repository.findByName(request.getName())
//                .filter(existing -> !existing.getId().equals(id))
//                .ifPresent(existing -> {
//                    throw new IllegalStateException(
//                            "Employee with name '" + request.getName() + "' already exists"
//                    );
//                });
//
//        entity.setName(request.getName());
//        entity.setDescription(request.getDescription());
//
//        EmployeeEntity updatedEntity = repository.save(entity);
//
//        return  EmployeeResponseDTO.fromEntity(updatedEntity);
//    }

    @Transactional
    public void delete(Long id, boolean soft) {
        EmployeeEntity entity = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Employee not found with id: " + id
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
