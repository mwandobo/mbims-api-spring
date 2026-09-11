package com.mwalimubank.mbimsapi.features.common;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.util.List;

public class PageSpecs {

    private PageSpecs() {}

    public static <T> Specification<T> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    public static <T> Specification<T> searchLike(String search, String... fields) {
        if (search == null || search.isBlank() || fields == null || fields.length == 0) {
            return null;
        }

        String term = search.trim();
        String likePattern = "%" + term.toLowerCase() + "%";
        boolean isNumeric = term.matches("-?\\d+");

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            for (String field : fields) {
                Path<?> path = root.get(field);
                Class<?> javaType = path.getJavaType();

                if (String.class.equals(javaType)) {
                    predicates.add(cb.like(cb.lower(path.as(String.class)), likePattern));
                    continue;
                }

                // Numbers: only match when user typed a number
                if (isNumeric && Number.class.isAssignableFrom(javaType)) {
                    try {
                        if (javaType.equals(Integer.class) || javaType.equals(int.class)) {
                            predicates.add(cb.equal(path, Integer.valueOf(term)));
                        } else if (javaType.equals(Long.class) || javaType.equals(long.class)) {
                            predicates.add(cb.equal(path, Long.valueOf(term)));
                        } else if (javaType.equals(Short.class) || javaType.equals(short.class)) {
                            predicates.add(cb.equal(path, Short.valueOf(term)));
                        } else {
                            // fallback: compare as long
                            predicates.add(cb.equal(path, Long.valueOf(term)));
                        }
                    } catch (NumberFormatException ignored) {
                        // skip this field
                    }
                    continue;
                }

                // Optional: non-string, non-numeric term → try casting to string (DB-dependent)
                // Uncomment if you want "12" to also match stringified numbers via LIKE:
                // Expression<String> asString = path.as(String.class);
                // predicates.add(cb.like(cb.lower(asString), likePattern));
            }

            if (predicates.isEmpty()) {
                return cb.disjunction(); // no match
            }
            return cb.or(predicates.toArray(new Predicate[0]));
        };
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
