package com.mwalimubank.mbimsapi.features.transaction.service;

import com.mwalimubank.mbimsapi.features.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TransactionSyncService {

    private final TransactionRepository transactionRepository;
    private final TransactionSyncService self;

    // Manual constructor with @Lazy on the self-injection
    public TransactionSyncService(
            TransactionRepository transactionRepository,
            @Lazy TransactionSyncService self) {
        this.transactionRepository = transactionRepository;
        this.self = self;
    }

    @Async
    public void syncTransactionsAsync() {
        log.info("Async transaction sync started...");
        try {
            int processed = self.syncTransactions();
            log.info("Async transaction sync finished. Processed: {}", processed);
        } catch (Exception e) {
            log.error("Async transaction sync failed", e);
        }
    }

    @Transactional
    public int syncTransactions() {
        log.info("Starting transaction sync from PROFITS.GLI_TRX_EXTRACT → MBIMS.transaction...");
        int processed = transactionRepository.upsertAllRelevantTransactions();
        log.info("Transaction sync completed. Rows affected: {}", processed);
        return processed;
    }

    @Scheduled(cron = "0 0 22 * * *")
    public void scheduledTransactionSync() {
        log.info("=== Scheduled Transaction Sync started ===");
        try {
            int processed = self.syncTransactions();
            log.info("=== Scheduled Transaction Sync finished. Processed: {} ===", processed);
        } catch (Exception e) {
            log.error("=== Scheduled Transaction Sync failed ===", e);
        }
    }
}