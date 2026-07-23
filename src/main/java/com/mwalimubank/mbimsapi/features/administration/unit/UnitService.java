package com.mwalimubank.mbimsapi.features.administration.unit;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.unit.dto.CreateUnitDTO;
import com.mwalimubank.mbimsapi.features.administration.unit.dto.UnitResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.unit.UnitEntity;
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

    public PagedResponse<UnitResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<UnitEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        // Add search logic here if needed

        Page<UnitEntity> page = repository.findAll(spec, pagination.toPageable());

        List<UnitResponseDTO> result = page.getContent().stream()
                .map(UnitResponseDTO::fromEntity)
                .toList();

        return new PagedResponse<>(
                result,
                new PaginationDto(page.getTotalElements(), page.getNumber() + 1, page.getSize(), page.getTotalPages()),
                false
        );
    }

    @Transactional
    public UnitResponseDTO create(CreateUnitDTO request) {
        UnitEntity entity = new UnitEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setManager(request.getManager());
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
        entity.setManager(request.getManager());
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
