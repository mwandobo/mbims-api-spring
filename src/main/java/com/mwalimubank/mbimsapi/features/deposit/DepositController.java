package com.mwalimubank.mbimsapi.features.deposit;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.deposit.dto.CreateDepositDTO;
import com.mwalimubank.mbimsapi.features.deposit.dto.DepositResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.deposit.service.DepositSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/deposits")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService service;
    private final DepositSyncService depositSyncService;

    @GetMapping
    public PagedResponse<DepositResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search) {
        return service.findAll(pagination, search);
    }

    @PostMapping
    public DepositResponseDTO create(@RequestBody CreateDepositDTO request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<DepositResponseDTO> findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public DepositResponseDTO update(@PathVariable Long id, @RequestBody CreateDepositDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestParam(name = "soft", defaultValue = "false") boolean soft) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }

    @PostMapping("/sync")
    public ApiResponse<String> syncDeposits() {
        depositSyncService.syncDepositsAsync();
        return ApiResponse.success("Deposit sync started");
    }
}
