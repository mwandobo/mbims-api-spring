package com.mwalimubank.mbimsapi.features.reconciliation;

import com.mwalimubank.mbimsapi.features.reconciliation.entity.ReconciliationEntity;
import com.mwalimubank.mbimsapi.features.reconciliation.repository.ReconciliationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReconciliationHelper {

    private static final String CODE_PREFIX = "RECON";
    private static final int CODE_WIDTH = 4;

    private final ReconciliationRepository repository;

    public String nextReconCode() {
        return repository
                .findFirstByCodeStartingWithOrderByIdDesc(CODE_PREFIX)
                .map(ReconciliationEntity::getCode)
                .map(this::incrementCode)
                .orElse(CODE_PREFIX + String.format("%0" + CODE_WIDTH + "d", 1));
    }

    private String incrementCode(String lastCode) {
        String numberPart = lastCode.substring(CODE_PREFIX.length()).replaceAll("\\D", "");
        int next = numberPart.isEmpty() ? 1 : Integer.parseInt(numberPart) + 1;
        return CODE_PREFIX + String.format("%0" + CODE_WIDTH + "d", next);
    }
}