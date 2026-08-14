package com.mwalimubank.mbimsapi.features.common.customer_address.service;

import com.mwalimubank.mbimsapi.features.common.customer_address.CustomerAddressRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CustomerAddressSyncService {

    private final CustomerAddressRepository customerAddressRepository;
    private final CustomerAddressSyncService self;

    public CustomerAddressSyncService(
            CustomerAddressRepository customerAddressRepository,
            @Lazy CustomerAddressSyncService self) {
        this.customerAddressRepository = customerAddressRepository;
        this.self = self;
    }

    @Async
    public void syncCustomerAddressesAsync() {
        log.info("Async customer address sync started...");
        try {
            int processed = self.syncCustomerAddresses();
            log.info("Async customer address sync finished. Processed: {}", processed);
        } catch (Exception e) {
            log.error("Async customer address sync failed", e);
        }
    }

    @Transactional
    public int syncCustomerAddresses() {
        log.info("Starting customer address sync from PROFITS.CUST_ADDRESS → MBIMS.customer_address...");
        int processed = customerAddressRepository.upsertAllCustomerAddresses();
        log.info("Customer address sync completed. Rows affected: {}", processed);
        return processed;
    }

    @Scheduled(cron = "0 30 21 * * *")   // 9:30 PM (after customer sync)
    public void scheduledCustomerAddressSync() {
        log.info("=== Scheduled Customer Address Sync started ===");
        try {
            int processed = self.syncCustomerAddresses();
            log.info("=== Scheduled Customer Address Sync finished. Processed: {} ===", processed);
        } catch (Exception e) {
            log.error("=== Scheduled Customer Address Sync failed ===", e);
        }
    }
}