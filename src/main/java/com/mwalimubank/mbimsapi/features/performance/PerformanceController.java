package com.mwalimubank.mbimsapi.features.performance;

import com.mwalimubank.mbimsapi.features.performance.dto.IndividualPerformanceDTO;
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


    @GetMapping("/unit-stats")
    public ResponseEntity<List<UnitPerformanceDTO>> getUnitStats(
            @RequestParam(defaultValue = "4") int limit) {

        return ResponseEntity.ok(service.findTopUnitStats(limit));
    }

    @GetMapping("/unit-stats/all")
    public ResponseEntity<List<UnitPerformanceDTO>> getAllUnitStats() {
        return ResponseEntity.ok(service.findAllUnitStats());
    }

    @GetMapping("/individual-stats")
    public ResponseEntity<List<IndividualPerformanceDTO>> getIndividualStats(
            @RequestParam(defaultValue = "4") int limit) {
        return ResponseEntity.ok(service.findTopIndividualStats(limit));
    }

    @GetMapping("/individual-stats/all")
    public ResponseEntity<List<IndividualPerformanceDTO>> getAllIndividualStats() {
        return ResponseEntity.ok(service.findAllIndividualStats());
    }

}
