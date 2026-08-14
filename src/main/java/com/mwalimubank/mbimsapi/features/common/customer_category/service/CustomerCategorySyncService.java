package com.mwalimubank.mbimsapi.features.common.customer_category.service;

import com.mwalimubank.mbimsapi.features.common.customer_category.CustomerCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CustomerCategorySyncService {

    private final CustomerCategoryRepository customerCategoryRepository;
    private final CustomerCategorySyncService self;

    public CustomerCategorySyncService(
            CustomerCategoryRepository customerCategoryRepository,
            @Lazy CustomerCategorySyncService self) {
        this.customerCategoryRepository = customerCategoryRepository;
        this.self = self;
    }

    @Async
    public void syncCustomerCategoriesAsync() {
        log.info("Async customer category sync started...");
        try {
            int processed = self.syncCustomerCategories();
            log.info("Async customer category sync finished. Processed: {}", processed);
        } catch (Exception e) {
            log.error("Async customer category sync failed", e);
        }
    }

    @Transactional
    public int syncCustomerCategories() {
        log.info("Starting customer category sync from PROFITS.CUSTOMER_CATEGORY → MBIMS.customer_category...");
        int processed = customerCategoryRepository.upsertAllCustomerCategories();
        log.info("Customer category sync completed. Rows affected: {}", processed);
        return processed;
    }

    @Scheduled(cron = "0 45 21 * * *")   // 9:45 PM
    public void scheduledCustomerCategorySync() {
        log.info("=== Scheduled Customer Category Sync started ===");
        try {
            int processed = self.syncCustomerCategories();
            log.info("=== Scheduled Customer Category Sync finished. Processed: {} ===", processed);
        } catch (Exception e) {
            log.error("=== Scheduled Customer Category Sync failed ===", e);
        }
    }
}