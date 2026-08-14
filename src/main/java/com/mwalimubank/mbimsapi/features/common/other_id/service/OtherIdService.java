package com.mwalimubank.mbimsapi.features.common.other_id.service;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.common.other_id.OtherIdRepository;
import com.mwalimubank.mbimsapi.features.common.other_id.dto.CreateOtherIdDTO;
import com.mwalimubank.mbimsapi.features.common.other_id.dto.OtherIdResponseDTO;
import com.mwalimubank.mbimsapi.features.common.other_id.OtherIdEntity;
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
public class OtherIdService {
    private final OtherIdRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;

    public PagedResponse<OtherIdResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<OtherIdEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        // Add search logic here if needed

        Page<OtherIdEntity> page = repository.findAll(spec, pagination.toPageable());

        List<OtherIdResponseDTO> result = page.getContent().stream()
                .map(OtherIdResponseDTO::fromEntity)
                .toList();

        return new PagedResponse<>(
                result,
                new PaginationDto(page.getTotalElements(), page.getNumber() + 1, page.getSize(), page.getTotalPages()),
                false
        );
    }

    @Transactional
    public OtherIdResponseDTO create(CreateOtherIdDTO request) {
        OtherIdEntity entity = new OtherIdEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        OtherIdEntity saved = repository.save(entity);
        return OtherIdResponseDTO.fromEntity(saved);
    }

    public ApprovalAwareDTO<OtherIdResponseDTO> findOne(Long id) {
        OtherIdEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("OtherId not found"));
        return approvalStatusUtil.attachApprovalInfo(
                OtherIdResponseDTO.fromEntity(entity),
                entity.getId(),
                OtherIdEntity.class.getSimpleName(),
                currentUserService.getCurrentUserRoleId()
        );
    }

    @Transactional
    public OtherIdResponseDTO update(Long id, CreateOtherIdDTO request) {
        OtherIdEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("OtherId not found"));

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());

        OtherIdEntity updated = repository.save(entity);
        return OtherIdResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        OtherIdEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("OtherId not found"));
        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}
