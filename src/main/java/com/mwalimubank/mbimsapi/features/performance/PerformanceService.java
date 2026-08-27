package com.mwalimubank.mbimsapi.features.performance;


import com.mwalimubank.mbimsapi.features.performance.dto.CustomerStatsResponseDTO;
import com.mwalimubank.mbimsapi.features.performance.dto.CustomerStatusDTO;
import com.mwalimubank.mbimsapi.features.performance.dto.UnitPerformanceDTO;
import com.mwalimubank.mbimsapi.features.performance.repository.PerformanceCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public List<UnitPerformanceDTO> findTopUnitStats(int limit) {
        List<Object[]> rows = customerRepository.findTopUnitsByCustomerCount(limit);
        return mapToUnitPerformanceDTO(rows);
    }

    public List<UnitPerformanceDTO> findAllUnitStats() {
        List<Object[]> rows = customerRepository.findAllUnitsByCustomerCount();
        return mapToUnitPerformanceDTO(rows);
    }

    private List<UnitPerformanceDTO> mapToUnitPerformanceDTO(List<Object[]> rows) {
        List<UnitPerformanceDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            UnitPerformanceDTO dto = new UnitPerformanceDTO();
            dto.setUnitId(row[0] != null ? ((Number) row[0]).longValue() : null);
            dto.setUnitName(row[1] != null ? row[1].toString() : null);
            dto.setTotalCustomers(row[2] != null ? ((Number) row[2]).longValue() : 0L);
            dto.setActive(row[3] != null ? ((Number) row[3]).longValue() : 0L);
            dto.setDormant(row[4] != null ? ((Number) row[4]).longValue() : 0L);
            dto.setClosed(row[5] != null ? ((Number) row[5]).longValue() : 0L);
            result.add(dto);
        }
        return result;
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