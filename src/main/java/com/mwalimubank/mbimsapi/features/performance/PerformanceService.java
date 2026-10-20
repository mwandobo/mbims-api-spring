package com.mwalimubank.mbimsapi.features.performance;

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
                    dto.setDeposits(BigDecimal.valueOf(((Number) row[3]).doubleValue()));
                }
            } else {
                dto.setDeposits(BigDecimal.ZERO);
            }

            // Dummy for now
            dto.setLoans(dummyLoans);
            dto.setExpenses(dummyExpenses);

            result.add(dto);
        }
        return result;
    }
}