package com.mwalimubank.mbimsapi.features.approval.services;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.core.dto.PaginationResponse;
import com.mwalimubank.mbimsapi.features.approval.entity.SysApproval;
import com.mwalimubank.mbimsapi.features.approval.repository.SysApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysApprovalService {

    private final SysApprovalRepository repository;

    public PaginationResponse<SysApproval> findAll(PaginationRequest pagination, String search) {
        Specification<SysApproval> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));

        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%")
                    )
            );
        }

        Page<SysApproval> dataDtos = repository.findAll(spec, pagination.toPageable());
        return PaginationResponse.of(dataDtos);
    }
}