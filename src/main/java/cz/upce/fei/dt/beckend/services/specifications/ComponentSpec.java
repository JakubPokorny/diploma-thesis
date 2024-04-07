package cz.upce.fei.dt.beckend.services.specifications;

import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.Component_;
import cz.upce.fei.dt.beckend.services.filters.ComponentFilter;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Set;

public class ComponentSpec {
    public static Specification<Component> filterBy(ComponentFilter componentFilter) {
        return Specification
                .where(findAllStringLikeIgnoreCase(componentFilter.getNameFilter(), Component_.name))
                .and(findAllStringLikeIgnoreCase(componentFilter.getDescriptionFilter(), Component_.description))
                .and(findAllIntegerGreaterThan(componentFilter.getFromAmountFilter(), Component_.inStock))
                .and(findAllIntegerLessThan(componentFilter.getToAmountFilter(), Component_.inStock))
                .and(findAllIntegerGreaterThan(componentFilter.getFromMinAmountFilter(), Component_.minInStock))
                .and(findAllIntegerLessThan(componentFilter.getToMinAmountFilter(), Component_.minInStock))
                .and(findAllLocalDateTimeLessThan(componentFilter.getToUpdatedFilter(), Component_.updated))
                .and(findAllLocalDateTimeGreaterThan(componentFilter.getFromUpdatedFilter(), Component_.updated))
                .and(findAllSelectedProduct(componentFilter.getProductsFilter()))
                .and(findAllSelectedUsers(componentFilter.getUsersFilter()));
    }

    private static Specification<Component> findAllSelectedUsers(Set<Long> selectedUsers) {
        return (root, query, builder) -> selectedUsers == null || selectedUsers.isEmpty()
                ? null
                : root.get("user").get("id").in(selectedUsers);
    }
    private static Specification<Component> findAllSelectedProduct(Set<Long> selectedProducts) {
        return (root, query, builder) -> selectedProducts == null || selectedProducts.isEmpty()
                ? null
                : root.get("productComponents").get("id").get("productId").in(selectedProducts);
    }

    private static Specification<Component> findAllStringLikeIgnoreCase(String filter, SingularAttribute<Component, String> singularAttribute) {
        return (root, query, builder) -> filter == null || filter.isEmpty()
                ? null
                : builder.like(builder.lower(root.get(singularAttribute)), "%" + filter.toLowerCase() + "%");
    }

    private static Specification<Component> findAllIntegerGreaterThan(Integer filter, SingularAttribute<Component, Integer> singularAttribute) {
        return (root, query, builder) -> filter == null
                ? null
                : builder.greaterThanOrEqualTo(root.get(singularAttribute), filter);
    }

    private static Specification<Component> findAllIntegerLessThan(Integer filter, SingularAttribute<Component, Integer> singularAttribute) {
        return (root, query, builder) -> filter == null
                ? null
                : builder.lessThanOrEqualTo(root.get(singularAttribute), filter);
    }

    private static Specification<Component> findAllLocalDateTimeLessThan(LocalDateTime filter, SingularAttribute<Component, LocalDateTime> singularAttribute) {
        return (root, query, builder) -> filter == null
                ? null
                : builder.lessThanOrEqualTo(root.get(singularAttribute), filter);
    }

    private static Specification<Component> findAllLocalDateTimeGreaterThan(LocalDateTime filter, SingularAttribute<Component, LocalDateTime> singularAttribute) {
        return (root, query, builder) -> filter == null
                ? null
                : builder.greaterThanOrEqualTo(root.get(singularAttribute), filter);
    }
}
