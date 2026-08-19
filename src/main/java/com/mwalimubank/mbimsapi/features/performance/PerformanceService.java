package com.mwalimubank.mbimsapi.features.performance;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.customer.entity.CustomerEntity;
import com.mwalimubank.mbimsapi.features.performance.dto.CreatePerformanceDTO;
import com.mwalimubank.mbimsapi.features.performance.dto.CustomerStatsResponseDTO;
import com.mwalimubank.mbimsapi.features.performance.dto.CustomerStatusDTO;
import com.mwalimubank.mbimsapi.features.performance.dto.PerformanceResponseDTO;
import com.mwalimubank.mbimsapi.features.performance.entities.PerformanceEntity;
import com.mwalimubank.mbimsapi.features.performance.repository.PerformanceCustomerRepository;
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
    private final PerformanceCustomerRepository customerRepository;
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





//    public CustomerStatsResponseDTO findCustomers() {
//        List<CustomerEntity> allCustomers = customerRepository.findAll();
//        List<CustomerEntity> individualCustomers = customerRepository.findByCustType("1");
//        List<CustomerEntity> corporateCustomers = customerRepository.findByCustType("2");
//        List<CustomerEntity> corporateCustomersV1 = customerRepository.findByCustType("3");
//
//        // Combine corporate lists
//        List<CustomerEntity> allCorporateCustomers = new ArrayList<>(corporateCustomers);
//        allCorporateCustomers.addAll(corporateCustomersV1);
//
//        long totalAllCustomers = allCustomers.size();
//        long totalIndividualCustomers =  individualCustomers.size();
//        long totalCorporateCustomers = allCorporateCustomers.size();;
//
//        CustomerStatsResponseDTO customerStatsResponse = new CustomerStatsResponseDTO();
//        customerStatsResponse.setTotalAllCustomers(totalAllCustomers);
//        customerStatsResponse.setTotalIndividualCustomers(totalIndividualCustomers);
//        customerStatsResponse.setTotalCorporateCustomers(totalCorporateCustomers);
//
//        customerStatsResponse.setAllCustomersAttrs(fetchCustomerStatus(allCustomers));
//        customerStatsResponse.setIndividualCustomersAttrs(fetchCustomerStatus(individualCustomers));
//        customerStatsResponse.setCorporateCustomersAttrs(fetchCustomerStatus(allCorporateCustomers));
//
//        return customerStatsResponse;
//    }


    public CustomerStatsResponseDTO findCustomers() {
        CustomerStatsResponseDTO dto = new CustomerStatsResponseDTO();

        long totalAll = customerRepository.count();
        long totalIndividual = customerRepository.countByCustType("1");
        long totalCorporate = customerRepository.countByCustType("2")
                + customerRepository.countByCustType("3");

        dto.setTotalAllCustomers(totalAll);
        dto.setTotalIndividualCustomers(totalIndividual);
        dto.setTotalCorporateCustomers(totalCorporate);

//        dto.setAllCustomersAttrs(toStatusMap(customerRepository.countByStatus()));
//        dto.setIndividualCustomersAttrs(toStatusMap(customerRepository.countByStatusAndCustType("1")));
//        dto.setCorporateCustomersAttrs(toStatusMap(customerRepository.countByStatusCorporate()));

        dto.setAllCustomersAttrs(toStatusDto(customerRepository.countByStatus()));
        dto.setIndividualCustomersAttrs(toStatusDto(customerRepository.countByStatusAndCustType("1")));
        dto.setCorporateCustomersAttrs(toStatusDto(customerRepository.countByStatusCorporate()));

        return dto;
    }

//    private Map<String, Long> toStatusMap(List<Object[]> rows) {
//        Map<String, Long> map = new HashMap<>();
//        for (Object[] row : rows) {
//            String status = row[0] != null ? row[0].toString() : "UNKNOWN";
//            map.put(status, (Long) row[1]);
//        }
//        return map;
//    }

    private CustomerStatusDTO toStatusDto(List<Object[]> rows) {
        CustomerStatusDTO dto = new CustomerStatusDTO();

        for (Object[] row : rows) {
            String status = row[0] != null ? row[0].toString().trim() : "";
            long count = (Long) row[1];

            switch (status) {
                case "2" -> dto.setActive(count);
                case "1" -> dto.setDormant(count);
                case "S", "SUSPENDED" -> dto.setClosed(count);     // add other statuses you use
                default -> dto.setClosed(count); // or accumulate into "other"
            }
        }
        return dto;
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
