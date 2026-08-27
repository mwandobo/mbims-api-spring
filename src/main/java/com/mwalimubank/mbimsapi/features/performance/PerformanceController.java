package com.mwalimubank.mbimsapi.features.performance;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.performance.dto.CustomerStatsResponseDTO;
import com.mwalimubank.mbimsapi.features.performance.dto.UnitPerformanceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/performances")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService service;


    @GetMapping("/customer-stats")
    public  CustomerStatsResponseDTO findCustomers(
            PaginationRequest pagination,
            @RequestParam(required = false) String search) {
        return service.findCustomers();
    }

    @GetMapping("/unit-stats")
    public ResponseEntity<?> getUnitStats(
            @RequestParam(defaultValue = "4") int limit) {

        List<UnitPerformanceDTO> stats = service.findTopUnitStats(limit);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/unit-stats/all")
    public ResponseEntity<?> getAllUnitStats() {
        List<UnitPerformanceDTO> stats = service.findAllUnitStats();
        return ResponseEntity.ok(stats);
    }

}
