package com.mwalimubank.mbimsapi.features.customer;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.customer.dto.CreateCustomerDTO;
import com.mwalimubank.mbimsapi.features.customer.dto.CustomerResponseDTO;
import com.mwalimubank.mbimsapi.features.customer.CustomerEntity;
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

    public PagedResponse<CustomerResponseDTO> findAll(PaginationRequest pagination, String search) {
//        Specification<CustomerEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        // Add search logic here if needed

        Page<CustomerEntity> page = repository.findAll( pagination.toPageable());

        List<CustomerResponseDTO> result = page.getContent().stream()
                .map(CustomerResponseDTO::fromEntity)
                .toList();

        return new PagedResponse<>(
                result,
                new PaginationDto(page.getTotalElements(), page.getNumber() + 1, page.getSize(), page.getTotalPages()),
                false
        );
    }

    @Transactional
    public CustomerResponseDTO create(CreateCustomerDTO request) {
        CustomerEntity entity = new CustomerEntity();
        entity.setFirstName(request.getName());
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

        entity.setFirstName(request.getName());

        CustomerEntity updated = repository.save(entity);
        return CustomerResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        CustomerEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));
        if (soft) {
            repository.delete(entity);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}
