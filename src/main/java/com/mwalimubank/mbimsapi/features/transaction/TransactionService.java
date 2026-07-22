package com.mwalimubank.mbimsapi.features.transaction;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.transaction.dto.CreateTransactionDTO;
import com.mwalimubank.mbimsapi.features.transaction.dto.TransactionResponseDTO;
import com.mwalimubank.mbimsapi.features.transaction.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;

    public List<TransactionEntity> findAll(PaginationRequest pagination, String search) {
//        Specification<TransactionEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        // Add search logic here if needed


        return repository.findAll();
    }

    public List<TransactionEntity> findUserTransaction(long custID) {
//         return repository.findAllById(custID);

        log.info("Customer ID ID: {}", custID);


        return repository.findByCustId((int) custID);

//        Specification<TransactionEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        // Add search logic here if needed

    }


}
