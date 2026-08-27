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
    private final PerformanceCustomerRepository customerRepository;

    public CustomerStatsResponseDTO findCustomers() {
        CustomerStatsResponseDTO dto = new CustomerStatsResponseDTO();

        long totalAll = customerRepository.count();
        long totalIndividual = customerRepository.countByCustType("1");
        long totalCorporate = customerRepository.countByCustType("2")
                + customerRepository.countByCustType("3");

        dto.setTotalAllCustomers(totalAll);
        dto.setTotalIndividualCustomers(totalIndividual);
        dto.setTotalCorporateCustomers(totalCorporate);

        dto.setAllCustomersAttrs(toStatusDto(customerRepository.countByStatus()));
        dto.setIndividualCustomersAttrs(toStatusDto(customerRepository.countByStatusAndCustType("1")));
        dto.setCorporateCustomersAttrs(toStatusDto(customerRepository.countByStatusCorporate()));

        return dto;
    }

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
}