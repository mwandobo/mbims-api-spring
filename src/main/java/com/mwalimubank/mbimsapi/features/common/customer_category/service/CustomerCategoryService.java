package com.mwalimubank.mbimsapi.features.common.customer_category.service;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.common.customer_category.CustomerCategoryRepository;
import com.mwalimubank.mbimsapi.features.common.customer_category.dto.CreateCustomerCategoryDTO;
import com.mwalimubank.mbimsapi.features.common.customer_category.dto.CustomerCategoryResponseDTO;
import com.mwalimubank.mbimsapi.features.common.customer_category.CustomerCategoryEntity;
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
public class CustomerCategoryService {
    private final CustomerCategoryRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;

    public PagedResponse<CustomerCategoryResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<CustomerCategoryEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        // Add search logic here if needed

        Page<CustomerCategoryEntity> page = repository.findAll(spec, pagination.toPageable());

        List<CustomerCategoryResponseDTO> result = page.getContent().stream()
                .map(CustomerCategoryResponseDTO::fromEntity)
                .toList();

        return new PagedResponse<>(
                result,
                new PaginationDto(page.getTotalElements(), page.getNumber() + 1, page.getSize(), page.getTotalPages()),
                false
        );
    }

    @Transactional
    public CustomerCategoryResponseDTO create(CreateCustomerCategoryDTO request) {
        CustomerCategoryEntity entity = new CustomerCategoryEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        CustomerCategoryEntity saved = repository.save(entity);
        return CustomerCategoryResponseDTO.fromEntity(saved);
    }

    public ApprovalAwareDTO<CustomerCategoryResponseDTO> findOne(Long id) {
        CustomerCategoryEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("CustomerCategory not found"));
        return approvalStatusUtil.attachApprovalInfo(
                CustomerCategoryResponseDTO.fromEntity(entity),
                entity.getId(),
                CustomerCategoryEntity.class.getSimpleName(),
                currentUserService.getCurrentUserRoleId()
        );
    }

    @Transactional
    public CustomerCategoryResponseDTO update(Long id, CreateCustomerCategoryDTO request) {
        CustomerCategoryEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("CustomerCategory not found"));

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());

        CustomerCategoryEntity updated = repository.save(entity);
        return CustomerCategoryResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        CustomerCategoryEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("CustomerCategory not found"));
        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}
