package com.mwalimubank.mbimsapi.features.transaction.service;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.features.transaction.entity.TransactionFromBankEntity;
import com.mwalimubank.mbimsapi.features.transaction.repository.TransactionFromBankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionFromBankService {
    private final TransactionFromBankRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;

    public List<TransactionFromBankEntity> findAll(PaginationRequest pagination, String search) {
//        Specification<TransactionEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        // Add search logic here if needed


        return repository.findAll();
    }

    public List<TransactionFromBankEntity> findUserTransaction(long custID) {
//         return repository.findAllById(custID);

        log.info("Customer ID ID: {}", custID);


        return repository.findByCustId((int) custID);

//        Specification<TransactionEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        // Add search logic here if needed

    }


}
