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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PagedQueryService {

    private final ApprovalStatusUtil approvalStatusUtil;

    /** Simple call — no sort aliases */
    public <E, D> PagedResponse<D> findAll(
            JpaSpecificationExecutor<E> repository,
            Specification<E> spec,
            PaginationRequest pagination,
            Class<E> entityClass,
            Function<E, Long> idGetter,
            Function<E, D> mapper,
            BiConsumer<D, String> approvalSetter,
            Set<String> allowedSortFields
    ) {
        return findAll(
                repository, spec, pagination, entityClass,
                idGetter, mapper, approvalSetter,
                allowedSortFields, Map.of()
        );
    }

    /** Full call — with sort aliases (e.g. departmentName → department.name) */
    public <E, D> PagedResponse<D> findAll(
            JpaSpecificationExecutor<E> repository,
            Specification<E> spec,
            PaginationRequest pagination,
            Class<E> entityClass,
            Function<E, Long> idGetter,
            Function<E, D> mapper,
            BiConsumer<D, String> approvalSetter,
            Set<String> allowedSortFields,
            Map<String, String> sortAliases          // ← this makes the signature different
    ) {
        boolean hasApprovalMode = approvalStatusUtil.hasApprovalMode(entityClass.getSimpleName());

        Pageable pageable = sanitizePageable(
                pagination.toPageable(),
                allowedSortFields,
                sortAliases
        );

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

    private Pageable sanitizePageable(
            Pageable pageable,
            Set<String> allowed,
            Map<String, String> sortAliases
    ) {
        if (!pageable.getSort().isSorted()) {
            return pageable;
        }

        Sort.Order order = pageable.getSort().iterator().next();
        String property = order.getProperty();

        if (sortAliases != null && sortAliases.containsKey(property)) {
            property = sortAliases.get(property);
        }

        if (allowed != null && !allowed.isEmpty() && !allowed.contains(property)) {
            return PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "id")
            );
        }

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(order.getDirection(), property)
        );
    }
}