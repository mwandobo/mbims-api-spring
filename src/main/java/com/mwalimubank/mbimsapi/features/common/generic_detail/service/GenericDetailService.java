package com.mwalimubank.mbimsapi.features.common.generic_detail.service;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.common.generic_detail.GenericDetailRepository;
import com.mwalimubank.mbimsapi.features.common.generic_detail.dto.CreateGenericDetailDTO;
import com.mwalimubank.mbimsapi.features.common.generic_detail.dto.GenericDetailResponseDTO;
import com.mwalimubank.mbimsapi.features.common.generic_detail.GenericDetailEntity;
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
public class GenericDetailService {
    private final GenericDetailRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;

    public PagedResponse<GenericDetailResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<GenericDetailEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        // Add search logic here if needed

        Page<GenericDetailEntity> page = repository.findAll(spec, pagination.toPageable());

        List<GenericDetailResponseDTO> result = page.getContent().stream()
                .map(GenericDetailResponseDTO::fromEntity)
                .toList();

        return new PagedResponse<>(
                result,
                new PaginationDto(page.getTotalElements(), page.getNumber() + 1, page.getSize(), page.getTotalPages()),
                false
        );
    }

    @Transactional
    public GenericDetailResponseDTO create(CreateGenericDetailDTO request) {
        GenericDetailEntity entity = new GenericDetailEntity();

        GenericDetailEntity saved = repository.save(entity);
        return GenericDetailResponseDTO.fromEntity(saved);
    }

    public ApprovalAwareDTO<GenericDetailResponseDTO> findOne(Long id) {
        GenericDetailEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("GenericDetail not found"));
        return approvalStatusUtil.attachApprovalInfo(
                GenericDetailResponseDTO.fromEntity(entity),
                entity.getId(),
                GenericDetailEntity.class.getSimpleName(),
                currentUserService.getCurrentUserRoleId()
        );
    }

    @Transactional
    public GenericDetailResponseDTO update(Long id, CreateGenericDetailDTO request) {
        GenericDetailEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("GenericDetail not found"));

        GenericDetailEntity updated = repository.save(entity);
        return GenericDetailResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        GenericDetailEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("GenericDetail not found"));
        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}
