package com.mwalimubank.mbimsapi.features.customer.services;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.administration.department.dto.DepartmentResponseDTO;
import com.mwalimubank.mbimsapi.features.common.PageSpecs;
import com.mwalimubank.mbimsapi.features.common.services.PagedQueryService;
import com.mwalimubank.mbimsapi.features.customer.dto.CreateCustomerDTO;
import com.mwalimubank.mbimsapi.features.customer.dto.CustomerResponseDTO;
import com.mwalimubank.mbimsapi.features.customer.entity.CustomerEntity;
import com.mwalimubank.mbimsapi.features.customer.repository.CustomerRepository;
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
public class CustomerService {
    private final CustomerRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;
    private final PagedQueryService pagedQueryService;

    private static final Set<String> SORT_FIELDS = Set.of(
            "id", "name", "createdAt"
    );

    public PagedResponse<CustomerResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<CustomerEntity> spec = PageSpecs.and(
                PageSpecs.notDeleted(),
                PageSpecs.searchLike(search, "name", "createdAt")
        );

        return pagedQueryService.findAll(
                repository,
                spec,
                pagination,
                CustomerEntity.class,
                CustomerEntity::getId,
                CustomerResponseDTO::fromEntity,
                CustomerResponseDTO::setApprovalStatus,
                SORT_FIELDS
        );
    }

    @Transactional
    public CustomerResponseDTO create(CreateCustomerDTO request) {
        CustomerEntity entity = new CustomerEntity();
        entity.setName(request.getName());
        CustomerEntity saved = repository.save(entity);
        return CustomerResponseDTO.fromEntity(saved);
    }

    public ApprovalAwareDTO<CustomerResponseDTO> findOne(Long id) {
        CustomerEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));
        return approvalStatusUtil.attachApprovalInfo(
                CustomerResponseDTO.fromEntity(entity),
                entity.getId(),
                CustomerEntity.class.getSimpleName(),
                currentUserService.getCurrentUserRoleId()
        );
    }

    @Transactional
    public CustomerResponseDTO update(Long id, CreateCustomerDTO request) {
        CustomerEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));

        entity.setName(request.getName());
        CustomerEntity updated = repository.save(entity);
        return CustomerResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        CustomerEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));
        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}
