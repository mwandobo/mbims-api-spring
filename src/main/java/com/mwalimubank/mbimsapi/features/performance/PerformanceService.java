package com.mwalimubank.mbimsapi.features.performance;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.performance.dto.CreatePerformanceDTO;
import com.mwalimubank.mbimsapi.features.performance.dto.CustomerStatsResponseDTO;
import com.mwalimubank.mbimsapi.features.performance.dto.CustomerStatusDTO;
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
        List<CustomerEntity> allCustomers = customerRepository.findAll();
        List<CustomerEntity> individualCustomers = customerRepository.findByCustType("1");
        List<CustomerEntity> corporateCustomers = customerRepository.findByCustType("2");
        List<CustomerEntity> corporateCustomersV1 = customerRepository.findByCustType("3");

        // Combine corporate lists
        List<CustomerEntity> allCorporateCustomers = new ArrayList<>(corporateCustomers);
        allCorporateCustomers.addAll(corporateCustomersV1);

        long totalAllCustomers = allCustomers.size();
        long totalIndividualCustomers =  individualCustomers.size();
        long totalCorporateCustomers = allCorporateCustomers.size();;

        CustomerStatsResponseDTO customerStatsResponse = new CustomerStatsResponseDTO();
        customerStatsResponse.setTotalAllCustomers(totalAllCustomers);
        customerStatsResponse.setTotalIndividualCustomers(totalIndividualCustomers);
        customerStatsResponse.setTotalCorporateCustomers(totalCorporateCustomers);

        customerStatsResponse.setAllCustomersAttrs(fetchCustomerStatus(allCustomers));
        customerStatsResponse.setIndividualCustomersAttrs(fetchCustomerStatus(individualCustomers));
        customerStatsResponse.setCorporateCustomersAttrs(fetchCustomerStatus(allCorporateCustomers));

        return customerStatsResponse;
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

    private CustomerStatusDTO fetchCustomerStatus(List<CustomerEntity> customers) {

        long activeCustomers = customers.stream()
                .filter(customer -> Objects.equals(customer.getCustStatus(), "2"))
                .count();

        long dormantCustomers = customers.stream()
                .filter(customer -> Objects.equals(customer.getCustStatus(), "1"))
                .count();

        long closedCustomers = customers.size() - (activeCustomers + dormantCustomers) ;
        CustomerStatusDTO dto = new CustomerStatusDTO();
        dto.setActive(activeCustomers);
        dto.setDormant(dormantCustomers);
        dto.setClosed(closedCustomers);

        return dto;
    }
}
