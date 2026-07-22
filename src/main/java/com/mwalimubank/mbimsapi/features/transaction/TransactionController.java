package com.mwalimubank.mbimsapi.features.transaction;

import com.mwalimubank.mbimsapi.features.transaction.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    @GetMapping
    public List<TransactionEntity> findByCustomerId(
            @RequestParam long customerId) {
        return service.findUserTransaction(customerId);
    }

}
