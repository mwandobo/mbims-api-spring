package com.mwalimubank.mbimsapi.features.common;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class PageSpecs {

    private PageSpecs() {}

    public static <T> Specification<T> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    /**
     * fields examples:
     *   "name", "description"
     *   "department.name"          // ManyToOne
     *   "unit.name"
     */
    public static <T> Specification<T> searchLike(String search, String... fields) {
        if (search == null || search.isBlank() || fields == null || fields.length == 0) {
            return null;
        }

        String term = search.trim();
        String likePattern = "%" + term.toLowerCase() + "%";
        boolean isNumeric = term.matches("-?\\d+");

        return (root, query, cb) -> {
            // avoid duplicate rows when joining collections (not needed for ManyToOne, but safe)
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            for (String field : fields) {
                Path<?> path = resolvePath(root, field);
                Class<?> javaType = path.getJavaType();

                if (String.class.equals(javaType)) {
                    predicates.add(cb.like(cb.lower(path.as(String.class)), likePattern));
                    continue;
                }

                if (isNumeric && Number.class.isAssignableFrom(javaType)) {
                    try {
                        if (Integer.class.equals(javaType) || int.class.equals(javaType)) {
                            predicates.add(cb.equal(path, Integer.valueOf(term)));
                        } else if (Long.class.equals(javaType) || long.class.equals(javaType)) {
                            predicates.add(cb.equal(path, Long.valueOf(term)));
                        } else if (Short.class.equals(javaType) || short.class.equals(javaType)) {
                            predicates.add(cb.equal(path, Short.valueOf(term)));
                        } else {
                            predicates.add(cb.equal(path, Long.valueOf(term)));
                        }
                    } catch (NumberFormatException ignored) {
                        // skip
                    }
                }
            }

            if (predicates.isEmpty()) {
                return cb.disjunction();
            }
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    /** "department.name" → root.get("department").get("name") */
    private static Path<?> resolvePath(From<?, ?> root, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        Path<?> path = root;
        for (String part : parts) {
            path = path.get(part);
        }
        return path;
    }

    @SafeVarargs
    public static <T> Specification<T> and(Specification<T>... specs) {
        Specification<T> result = null;
        for (Specification<T> s : specs) {
            if (s == null) continue;
            result = result == null ? Specification.where(s) : result.and(s);
        }
        return result == null
                ? (root, query, cb) -> cb.conjunction()
                : result;
    }
}