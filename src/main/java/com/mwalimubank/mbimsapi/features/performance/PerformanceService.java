package com.mwalimubank.mbimsapi.features.performance;

import com.mwalimubank.mbimsapi.features.performance.dto.IndividualPerformanceDTO;
import com.mwalimubank.mbimsapi.features.performance.dto.UnitPerformanceDTO;
import com.mwalimubank.mbimsapi.features.performance.repository.PerformanceCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PerformanceService {
    private final PerformanceCustomerRepository customerRepository;

    public List<UnitPerformanceDTO> findTopUnitStats(int limit) {
        List<Object[]> rows = customerRepository.findTopUnitsPerformance(limit);
        return mapToUnitPerformanceDTO(rows);
    }

    public List<UnitPerformanceDTO> findAllUnitStats() {
        List<Object[]> rows = customerRepository.findAllUnitsPerformance();
        return mapToUnitPerformanceDTO(rows);
    }

    private List<UnitPerformanceDTO> mapToUnitPerformanceDTO(List<Object[]> rows) {
        List<UnitPerformanceDTO> result = new ArrayList<>();

        // Simple dummy values – change later when you have real tables
        BigDecimal dummyLoans    = BigDecimal.valueOf(5_670_000);
        BigDecimal dummyExpenses = BigDecimal.valueOf(890_000);

        for (Object[] row : rows) {
            UnitPerformanceDTO dto = new UnitPerformanceDTO();

            dto.setUnitId(row[0] != null ? ((Number) row[0]).longValue() : null);
            dto.setUnitName(row[2] != null ? row[2].toString() : null);
            dto.setUnitCode(row[1] != null ? row[1].toString() : null);
            dto.setCustomerOnboarding(row[3] != null ? ((Number) row[3]).longValue() : 0L);

            // Deposits – handle possible BigDecimal / Double / Long from the DB
            if (row[4] != null) {
                if (row[4] instanceof BigDecimal bd) {
                    dto.setDeposits(bd);
                } else {
                    dto.setDeposits(BigDecimal.valueOf(((Number) row[4]).doubleValue()));
                }
            } else {
                dto.setDeposits(BigDecimal.ZERO);
            }

            // Dummy for now
            dto.setLoans(BigDecimal.valueOf(0.00));
            dto.setExpenses(BigDecimal.valueOf(0.00));

            result.add(dto);
        }
        return result;
    }


    public List<IndividualPerformanceDTO> findTopIndividualStats(int limit) {
        List<Object[]> rows = customerRepository.findTopIndividualsPerformance(limit);
        return mapToIndividualPerformanceDTO(rows);
    }

    public List<IndividualPerformanceDTO> findAllIndividualStats() {
        List<Object[]> rows = customerRepository.findAllIndividualsPerformance();
        return mapToIndividualPerformanceDTO(rows);
    }

    private List<IndividualPerformanceDTO> mapToIndividualPerformanceDTO(List<Object[]> rows) {
        List<IndividualPerformanceDTO> result = new ArrayList<>();
        BigDecimal dummyLoans = BigDecimal.valueOf(5_820_000);

        for (Object[] row : rows) {
            IndividualPerformanceDTO dto = new IndividualPerformanceDTO();

            // [0] employee_id
            dto.setEmployeeId(row[0] != null ? ((Number) row[0]).longValue() : null);
            // [1] staff_no
            dto.setStaffNo(row[1] != null ? row[1].toString() : null);
            // [2] employee_name
            dto.setName(row[2] != null ? row[2].toString() : null);
            // [3] role_name
            dto.setRole(row[3] != null ? row[3].toString() : null);
            // [4] unit_name
            dto.setUnit(row[4] != null ? row[4].toString() : null);
            // [5] customer_count
            dto.setCustomerOnboarding(row[5] != null ? ((Number) row[5]).longValue() : 0L);

            // [6] deposit_total
            if (row.length > 6 && row[6] != null) {
                if (row[6] instanceof BigDecimal bd) {
                    dto.setDeposits(bd);
                } else {
                    dto.setDeposits(BigDecimal.valueOf(((Number) row[6]).doubleValue()));
                }
            } else {
                dto.setDeposits(BigDecimal.ZERO);
            }

            dto.setLoans(BigDecimal.valueOf(0.00));
            result.add(dto);
        }
        return result;
    }
}