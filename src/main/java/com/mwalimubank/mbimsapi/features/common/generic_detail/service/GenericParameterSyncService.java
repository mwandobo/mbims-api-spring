package com.mwalimubank.mbimsapi.features.common.generic_detail.service;

import com.mwalimubank.mbimsapi.features.common.generic_detail.GenericDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class GenericParameterSyncService {

    private final GenericDetailRepository genericParameterRepository;
    private final GenericParameterSyncService self;

    public GenericParameterSyncService(
            GenericDetailRepository genericParameterRepository,
            @Lazy GenericParameterSyncService self) {
        this.genericParameterRepository = genericParameterRepository;
        this.self = self;
    }

    @Async
    public void syncGenericParametersAsync() {
        log.info("Async generic parameter sync started...");
        try {
            int processed = self.syncGenericParameters();
            log.info("Async generic parameter sync finished. Processed: {}", processed);
        } catch (Exception e) {
            log.error("Async generic parameter sync failed", e);
        }
    }

    @Transactional
    public int syncGenericParameters() {
        log.info("Starting generic parameter sync from PROFITS.GENERIC_HEAD → MBIMS.generic_parameter...");
        int processed = genericParameterRepository.upsertAllGenericParameters();
        log.info("Generic parameter sync completed. Rows affected: {}", processed);
        return processed;
    }

    @Scheduled(cron = "0 0 20 * * *")   // 8:00 PM (before customer syncs)
    public void scheduledGenericParameterSync() {
        log.info("=== Scheduled Generic Parameter Sync started ===");
        try {
            int processed = self.syncGenericParameters();
            log.info("=== Scheduled Generic Parameter Sync finished. Processed: {} ===", processed);
        } catch (Exception e) {
            log.error("=== Scheduled Generic Parameter Sync failed ===", e);
        }
    }
}