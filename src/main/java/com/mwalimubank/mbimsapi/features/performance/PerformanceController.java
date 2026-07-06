package com.mwalimubank.mbimsapi.features.performance;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.performance.dto.CreatePerformanceDTO;
import com.mwalimubank.mbimsapi.features.performance.dto.CustomerStatsResponseDTO;
import com.mwalimubank.mbimsapi.features.performance.dto.PerformanceResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.performance.entities.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/performances")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService service;

    @GetMapping
    public PagedResponse<PerformanceResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search) {
        return service.findAll(pagination, search);
    }

    @PostMapping
    public PerformanceResponseDTO create(@RequestBody CreatePerformanceDTO request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<PerformanceResponseDTO> findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public PerformanceResponseDTO update(@PathVariable Long id, @RequestBody CreatePerformanceDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestParam(name = "soft", defaultValue = "false") boolean soft) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }

    @GetMapping("/customer-stats")
    public  CustomerStatsResponseDTO findCustomers(
            PaginationRequest pagination,
            @RequestParam(required = false) String search) {
        return service.findCustomers();
    }

}
