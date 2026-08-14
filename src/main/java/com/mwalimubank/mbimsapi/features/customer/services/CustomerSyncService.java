package com.mwalimubank.mbimsapi.features.customer.services;

import com.mwalimubank.mbimsapi.features.customer.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CustomerSyncService {

    private final CustomerRepository customerRepository;
    private final CustomerSyncService self;

    public CustomerSyncService(
            CustomerRepository customerRepository,
            @Lazy CustomerSyncService self) {
        this.customerRepository = customerRepository;
        this.self = self;
    }

    @Async
    public void syncCustomersAsync() {
        log.info("Async customer sync started...");
        try {
            int processed = self.syncCustomers();
            log.info("Async customer sync finished. Processed: {}", processed);
        } catch (Exception e) {
            log.error("Async customer sync failed", e);
        }
    }

    @Transactional
    public int syncCustomers() {
        log.info("Starting customer sync from PROFITS.CUSTOMER → MBIMS.customer...");
        int processed = customerRepository.upsertAllCustomers();
        log.info("Customer sync completed. Rows affected: {}", processed);
        return processed;
    }

    @Scheduled(cron = "0 0 21 * * *")
    public void scheduledCustomerSync() {
        log.info("=== Scheduled Customer Sync started at 9:00 PM ===");
        try {
            int processed = self.syncCustomers();
            log.info("=== Scheduled Customer Sync finished. Processed: {} ===", processed);
        } catch (Exception e) {
            log.error("=== Scheduled Customer Sync failed ===", e);
        }
    }
}