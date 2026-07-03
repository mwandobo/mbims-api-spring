package com.mwalimubank.mbimsapi.features.performance;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.performance.dto.CreatePerformanceDTO;
import com.mwalimubank.mbimsapi.features.performance.dto.CustomerStatsResponseDTO;
import com.mwalimubank.mbimsapi.features.performance.dto.PerformanceResponseDTO;
import com.mwalimubank.mbimsapi.features.performance.entities.CustomerEntity;
import com.mwalimubank.mbimsapi.features.performance.entities.PerformanceEntity;
import com.mwalimubank.mbimsapi.features.performance.repository.CustomerRepository;
import com.mwalimubank.mbimsapi.features.performance.repository.PerformanceRepository;
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
public class PerformanceService {
    private final PerformanceRepository repository;
    private final CustomerRepository customerRepository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;

    public PagedResponse<PerformanceResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<PerformanceEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        // Add search logic here if needed

        Page<PerformanceEntity> page = repository.findAll(spec, pagination.toPageable());

        List<PerformanceResponseDTO> result = page.getContent().stream()
                .map(PerformanceResponseDTO::fromEntity)
                .toList();

        return new PagedResponse<>(
                result,
                new PaginationDto(page.getTotalElements(), page.getNumber() + 1, page.getSize(), page.getTotalPages()),
                false
        );
    }

    @Transactional
    public PerformanceResponseDTO create(CreatePerformanceDTO request) {
        PerformanceEntity entity = new PerformanceEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        PerformanceEntity saved = repository.save(entity);
        return PerformanceResponseDTO.fromEntity(saved);
    }

    public ApprovalAwareDTO<PerformanceResponseDTO> findOne(Long id) {
        PerformanceEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Performance not found"));
        return approvalStatusUtil.attachApprovalInfo(
                PerformanceResponseDTO.fromEntity(entity),
                entity.getId(),
                PerformanceEntity.class.getSimpleName(),
                currentUserService.getCurrentUserRoleId()
        );
    }

    public CustomerStatsResponseDTO findCustomers() {
        List<CustomerEntity> customers = customerRepository.findAll();
        long totalCustomers = customers.size();
        long individualCustomers = customerRepository.countByCustType("1");
        long cooperateCustomers = totalCustomers - individualCustomers;

        CustomerStatsResponseDTO customerStatsDTO = new CustomerStatsResponseDTO();
        customerStatsDTO.setTotalCustomers(totalCustomers);
        customerStatsDTO.setTotalIndividualCustomers(individualCustomers);
        customerStatsDTO.setTotalCorporateCustomers(cooperateCustomers);
        return customerStatsDTO;
    }


    @Transactional
    public PerformanceResponseDTO update(Long id, CreatePerformanceDTO request) {
        PerformanceEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Performance not found"));

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());

        PerformanceEntity updated = repository.save(entity);
        return PerformanceResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        PerformanceEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Performance not found"));
        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}
