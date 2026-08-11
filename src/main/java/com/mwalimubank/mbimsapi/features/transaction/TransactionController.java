package com.mwalimubank.mbimsapi.features.transaction;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.transaction.dto.CreateTransactionDTO;
import com.mwalimubank.mbimsapi.features.transaction.dto.TransactionResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.transaction.entity.TransactionFromBankEntity;
import com.mwalimubank.mbimsapi.features.transaction.service.TransactionFromBankService;
import com.mwalimubank.mbimsapi.features.transaction.service.TransactionService;
import com.mwalimubank.mbimsapi.features.transaction.service.TransactionSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;
    private final TransactionSyncService transactionSyncService;
    private final TransactionFromBankService fromBankService;

    @GetMapping
    public PagedResponse<TransactionResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search) {
        return service.findAll(pagination, search);
    }

    @PostMapping
    public TransactionResponseDTO create(@RequestBody CreateTransactionDTO request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<TransactionResponseDTO> findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public TransactionResponseDTO update(@PathVariable Long id, @RequestBody CreateTransactionDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestParam(name = "soft", defaultValue = "false") boolean soft) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncTransactions() {
        transactionSyncService.syncTransactionsAsync();
        return ResponseEntity.accepted().body("Transaction sync started");
    }

    @GetMapping("/by-customer/{id}")
    public List<TransactionFromBankEntity> findByCustomerId(
            @RequestParam long customerId) {
        return fromBankService.findUserTransaction(customerId);
    }

}
