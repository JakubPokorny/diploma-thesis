package cz.upce.fei.dt.backend.services.specifications;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;


public class FilterUtil<T> {

    public Specification<T> findAllStringLikeIgnoreCase(String filter, String attribute) {
        return (root, _, builder) -> filter == null || filter.isEmpty()
                ? null
                : builder.like(builder.lower(root.get(attribute)), "%" + filter.toLowerCase() + "%");
    }

    public Specification<T> findAllLongEqual(Long filter, String attribute) {
        return (root, _, builder) -> filter == null
                ? null
                : builder.equal(root.get(attribute), filter);
    }

    public Specification<T> findAllDoubleGreaterThanOrEqualTo(Double filter, String attribute) {
        return (root, _, builder) -> filter == null
                ? null
                : builder.greaterThanOrEqualTo(root.get(attribute), filter);
    }

    public Specification<T> findAllDoubleLessThanOrEqualTo(Double filter, String attribute) {
        return (root, _, builder) -> filter == null
                ? null
                : builder.lessThanOrEqualTo(root.get(attribute), filter);
    }

    public Specification<T> findAllIntegerGreaterThanOrEqualTo(Integer filter, String attribute) {
        return (root, _, builder) -> filter == null
                ? null
                : builder.greaterThanOrEqualTo(root.get(attribute), filter);
    }

    public Specification<T> findAllIntegerLessThanOrEqualTo(Integer filter, String attribute) {
        return (root, _, builder) -> filter == null
                ? null
                : builder.lessThanOrEqualTo(root.get(attribute), filter);
    }

    public Specification<T> findAllLocalDateLessThanOrEqualTo(LocalDate filter, String singularAttribute) {
        return (root, _, builder) -> filter == null
                ? null
                : builder.lessThanOrEqualTo(root.get(singularAttribute), filter);
    }

    public Specification<T> findAllLocalDateGreaterThanOrEqualTo(LocalDate filter, String singularAttribute) {
        return (root, _, builder) -> filter == null
                ? null
                : builder.greaterThanOrEqualTo(root.get(singularAttribute), filter);
    }
}
