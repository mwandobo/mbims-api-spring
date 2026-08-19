package com.mwalimubank.mbimsapi.features.common.other_id.service;

import com.mwalimubank.mbimsapi.features.common.other_id.OtherIdRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OtherIdSyncService {

    private final OtherIdRepository otherIdRepository;
    private final OtherIdSyncService self;

    public OtherIdSyncService(
            OtherIdRepository otherIdRepository,
            @Lazy OtherIdSyncService self) {
        this.otherIdRepository = otherIdRepository;
        this.self = self;
    }

    @Async
    public void syncOtherIdsAsync() {
        log.info("Async other-id sync started...");
        try {
            int processed = self.syncOtherIds();
            log.info("Async other-id sync finished. Processed: {}", processed);
        } catch (Exception e) {
            log.error("Async other-id sync failed", e);
        }
    }

    @Transactional
    public int syncOtherIds() {
        log.info("Starting other-id sync from PROFITS.OTHER_ID → MBIMS.other_id...");
        int processed = otherIdRepository.upsertAllOtherIds();
        log.info("Other-id sync completed. Rows affected: {}", processed);
        return processed;
    }

    @Scheduled(cron = "0 15 22 * * *")   // 10:15 PM
    public void scheduledOtherIdSync() {
        log.info("=== Scheduled Other-ID Sync started ===");
        try {
            int processed = self.syncOtherIds();
            log.info("=== Scheduled Other-ID Sync finished. Processed: {} ===", processed);
        } catch (Exception e) {
            log.error("=== Scheduled Other-ID Sync failed ===", e);
        }
    }
}