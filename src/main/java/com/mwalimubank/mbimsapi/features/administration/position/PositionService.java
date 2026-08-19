package com.mwalimubank.mbimsapi.features.administration.position;


import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentRepository;
import com.mwalimubank.mbimsapi.features.administration.position.dto.CreatePositionDTO;
import com.mwalimubank.mbimsapi.features.administration.position.dto.PositionResponseDTO;
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
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;

    public PagedResponse<PositionResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<PositionEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        // Add search logic here if needed

        Page<PositionEntity> page = repository.findAll(spec, pagination.toPageable());

        List<PositionResponseDTO> result = page.getContent().stream()
                .map(PositionResponseDTO::fromEntity)
                .toList();

        return new PagedResponse<>(
                result,
                new PaginationDto(page.getTotalElements(), page.getNumber() + 1, page.getSize(), page.getTotalPages()),
                false
        );
    }

    @Transactional
    public PositionResponseDTO create(CreatePositionDTO request) {
        PositionEntity entity = new PositionEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        DepartmentEntity department = validateDepartmentExists(request.getDepartmentId());
        entity.setDepartment(department);
        PositionEntity saved = repository.save(entity);
        return PositionResponseDTO.fromEntity(saved);
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
