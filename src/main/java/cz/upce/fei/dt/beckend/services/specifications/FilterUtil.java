package cz.upce.fei.dt.beckend.services.specifications;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class FilterUtil<T>{

    public Specification<T> findAllStringLikeIgnoreCase(String filter, String attribute) {
        return (root, query, builder) -> filter == null || filter.isEmpty()
                ? null
                : builder.like(builder.lower(root.get(attribute)), "%" + filter.toLowerCase() + "%");
    }
    public Specification<T> findAllDoubleGreaterThanOrEqualTo(Double filter, String attribute) {
        return (root, query, builder) -> filter == null
                ? null
                : builder.greaterThanOrEqualTo(root.get(attribute), filter);
    }
    public Specification<T> findAllDoubleLessThanOrEqualTo(Double filter, String attribute) {
        return (root, query, builder) -> filter == null
                ? null
                : builder.lessThanOrEqualTo(root.get(attribute), filter);
    }

    public Specification<T> findAllIntegerGreaterThanOrEqualTo(Integer filter, String attribute) {
        return (root, query, builder) -> filter == null
                ? null
                : builder.greaterThanOrEqualTo(root.get(attribute), filter);
    }
    public Specification<T> findAllIntegerLessThanOrEqualTo(Integer filter, String attribute) {
        return (root, query, builder) -> filter == null
                ? null
                : builder.lessThanOrEqualTo(root.get(attribute), filter);
    }

    public Specification<T> findAllLocalDateTimeLessThanOrEqualTo(LocalDateTime filter, String singularAttribute) {
        return (root, query, builder) -> filter == null
                ? null
                : builder.lessThanOrEqualTo(root.get(singularAttribute), filter);
    }

    public Specification<T> findAllLocalDateTimeGreaterThanOrEqualTo(LocalDateTime filter, String singularAttribute) {
        return (root, query, builder) -> filter == null
                ? null
                : builder.greaterThanOrEqualTo(root.get(singularAttribute), filter);
    }
    public Specification<T> findAllLocalDateLessThanOrEqualTo(LocalDate filter, String singularAttribute) {
        return (root, query, builder) -> filter == null
                ? null
                : builder.lessThanOrEqualTo(root.get(singularAttribute), filter);
    }

    public Specification<T> findAllLocalDateGreaterThanOrEqualTo(LocalDate filter, String singularAttribute) {
        return (root, query, builder) -> filter == null
                ? null
                : builder.greaterThanOrEqualTo(root.get(singularAttribute), filter);
    }
}
