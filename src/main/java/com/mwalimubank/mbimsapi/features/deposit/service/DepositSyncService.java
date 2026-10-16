package com.mwalimubank.mbimsapi.features.deposit.service;

import com.mwalimubank.mbimsapi.features.deposit.DepositRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class DepositSyncService {

    private final DepositRepository depositRepository;
    private final DepositSyncService self;

    public DepositSyncService(
            DepositRepository depositRepository,
            @Lazy DepositSyncService self) {
        this.depositRepository = depositRepository;
        this.self = self;
    }

    @Async
    public void syncDepositsAsync() {
        log.info("Async deposit sync started...");
        try {
            int processed = self.syncDeposits();
            log.info("Async deposit sync finished. Processed: {}", processed);
        } catch (Exception e) {
            log.error("Async deposit sync failed", e);
        }
    }

    @Transactional
    public int syncDeposits() {
        log.info("Starting deposit sync from PROFITS.GLI_TRX_EXTRACT → MBIMS.transaction (deposit products)...");
        int processed = depositRepository.upsertDepositTransactions();
        log.info("Deposit sync completed. Rows affected: {}", processed);
        return processed;
    }

    @Scheduled(cron = "0 30 22 * * *")
    public void scheduledDepositSync() {
        log.info("=== Scheduled Deposit Sync started ===");
        try {
            int processed = self.syncDeposits();
            log.info("=== Scheduled Deposit Sync finished. Processed: {} ===", processed);
        } catch (Exception e) {
            log.error("=== Scheduled Deposit Sync failed ===", e);
        }
    }
}