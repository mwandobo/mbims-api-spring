package com.mwalimubank.mbimsapi.features.common;

import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;

public class PageSpecs {

    private PageSpecs() {}

    public static <T> Specification<T> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    public static <T> Specification<T> searchLike(String search, String... fields) {
        if (search == null || search.isBlank() || fields.length == 0) {
            return null;
        }
        String pattern = "%" + search.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                Arrays.stream(fields)
                        .map(f -> cb.like(cb.lower(root.get(f)), pattern))
                        .toArray(jakarta.persistence.criteria.Predicate[]::new)
        );
    }

    @SafeVarargs
    public static <T> Specification<T> and(Specification<T>... specs) {
        Specification<T> result = null;
        for (Specification<T> s : specs) {
            if (s == null) continue;
            result = result == null ? s : result.and(s);
        }
        return result == null ? (root, query, cb) -> cb.conjunction() : result;
    }
}
