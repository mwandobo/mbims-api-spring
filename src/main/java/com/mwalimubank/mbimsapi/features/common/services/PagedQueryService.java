package com.mwalimubank.mbimsapi.features.common.services;

import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationDto;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PagedQueryService {

    private final ApprovalStatusUtil approvalStatusUtil;

    public <E, D> PagedResponse<D> findAll(
            JpaSpecificationExecutor<E> repository,
            Specification<E> spec,
            PaginationRequest pagination,
            Class<E> entityClass,
            Function<E, Long> idGetter,
            Function<E, D> mapper,
            BiConsumer<D, String> approvalSetter,  // dto.setApprovalStatus
            Set<String> allowedSortFields
    ) {
        boolean hasApprovalMode = approvalStatusUtil.hasApprovalMode(entityClass.getSimpleName());

        Pageable pageable = sanitizePageable(pagination.toPageable(), allowedSortFields);

        Page<E> page = repository.findAll(spec, pageable);
        List<E> entities = page.getContent();

        Map<Long, String> statusMap = hasApprovalMode
                ? approvalStatusUtil.getBulkApprovalStatuses(
                entityClass.getSimpleName(),
                entities.stream().map(idGetter).toList()
        )
                : Collections.emptyMap();

        List<D> result = entities.stream()
                .map(entity -> {
                    D dto = mapper.apply(entity);
                    if (hasApprovalMode && approvalSetter != null) {
                        approvalSetter.accept(dto, statusMap.get(idGetter.apply(entity)));
                    }
                    return dto;
                })
                .toList();

        return new PagedResponse<>(
                result,
                new PaginationDto(
                        page.getTotalElements(),
                        page.getNumber() + 1,
                        page.getSize(),
                        page.getTotalPages()
                ),
                hasApprovalMode
        );
    }

    private Pageable sanitizePageable(Pageable pageable, Set<String> allowed) {
        if (allowed == null || allowed.isEmpty()) {
            return pageable;
        }

        Sort sort = pageable.getSort();
        if (!sort.isSorted()) {
            return pageable;
        }

        Sort.Order order = sort.iterator().next();
        if (!allowed.contains(order.getProperty())) {
            return PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "id")
            );
        }
        return pageable;
    }
}