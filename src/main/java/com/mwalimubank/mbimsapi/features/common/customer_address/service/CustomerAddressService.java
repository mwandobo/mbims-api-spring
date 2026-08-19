package com.mwalimubank.mbimsapi.features.common.customer_address.service;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.common.customer_address.CustomerAddressRepository;
import com.mwalimubank.mbimsapi.features.common.customer_address.dto.CreateCustomerAddressDTO;
import com.mwalimubank.mbimsapi.features.common.customer_address.dto.CustomerAddressResponseDTO;
import com.mwalimubank.mbimsapi.features.common.customer_address.CustomerAddressEntity;
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
public class CustomerAddressService {
    private final CustomerAddressRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;

    public PagedResponse<CustomerAddressResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<CustomerAddressEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        // Add search logic here if needed

        Page<CustomerAddressEntity> page = repository.findAll(spec, pagination.toPageable());

        List<CustomerAddressResponseDTO> result = page.getContent().stream()
                .map(CustomerAddressResponseDTO::fromEntity)
                .toList();

        return new PagedResponse<>(
                result,
                new PaginationDto(page.getTotalElements(), page.getNumber() + 1, page.getSize(), page.getTotalPages()),
                false
        );
    }

    @Transactional
    public CustomerAddressResponseDTO create(CreateCustomerAddressDTO request) {
        CustomerAddressEntity entity = new CustomerAddressEntity();
        CustomerAddressEntity saved = repository.save(entity);
        return CustomerAddressResponseDTO.fromEntity(saved);
    }

    public ApprovalAwareDTO<CustomerAddressResponseDTO> findOne(Long id) {
        CustomerAddressEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("CustomerAddress not found"));
        return approvalStatusUtil.attachApprovalInfo(
                CustomerAddressResponseDTO.fromEntity(entity),
                entity.getId(),
                CustomerAddressEntity.class.getSimpleName(),
                currentUserService.getCurrentUserRoleId()
        );
    }

    @Transactional
    public CustomerAddressResponseDTO update(Long id, CreateCustomerAddressDTO request) {
        CustomerAddressEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("CustomerAddress not found"));

        CustomerAddressEntity updated = repository.save(entity);
        return CustomerAddressResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        CustomerAddressEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("CustomerAddress not found"));
        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}
