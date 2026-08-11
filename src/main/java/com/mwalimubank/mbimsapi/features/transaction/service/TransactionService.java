package com.mwalimubank.mbimsapi.features.transaction.service;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.transaction.dto.CreateTransactionDTO;
import com.mwalimubank.mbimsapi.features.transaction.dto.TransactionResponseDTO;
import com.mwalimubank.mbimsapi.features.transaction.entity.TransactionEntity;
import com.mwalimubank.mbimsapi.features.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationDto;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;

    public PagedResponse<TransactionResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<TransactionEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));

        // Add search logic here if needed

        Page<TransactionEntity> page = repository.findAll(spec, pagination.toPageable());

        List<TransactionResponseDTO> result = page.getContent().stream()
                .map(TransactionResponseDTO::fromEntity)
                .toList();

        return new PagedResponse<>(
                result,
                new PaginationDto(page.getTotalElements(), page.getNumber() + 1, page.getSize(), page.getTotalPages()),
                false
        );
    }

    @Transactional
    public TransactionResponseDTO create(CreateTransactionDTO request) {
        TransactionEntity entity = new TransactionEntity();
        TransactionEntity saved = repository.save(entity);
        return TransactionResponseDTO.fromEntity(saved);
    }

    public ApprovalAwareDTO<TransactionResponseDTO> findOne(Long id) {
        TransactionEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Transaction not found"));
        return approvalStatusUtil.attachApprovalInfo(
                TransactionResponseDTO.fromEntity(entity),
                entity.getId(),
                TransactionEntity.class.getSimpleName(),
                currentUserService.getCurrentUserRoleId()
        );
    }

    @Transactional
    public TransactionResponseDTO update(Long id, CreateTransactionDTO request) {
        TransactionEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Transaction not found"));


        TransactionEntity updated = repository.save(entity);
        return TransactionResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        TransactionEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Transaction not found"));
        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}
