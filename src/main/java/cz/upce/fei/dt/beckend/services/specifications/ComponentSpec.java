package cz.upce.fei.dt.beckend.services.specifications;

import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.Component_;
import cz.upce.fei.dt.beckend.services.filters.ComponentFilter;
import cz.upce.fei.dt.beckend.services.filters.ComponentTag;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public class ComponentSpec {
    private static final FilterUtil<Component> FILTER_UTIL = new FilterUtil<>();

    public static Specification<Component> filterBy(ComponentFilter componentFilter) {
        return Specification
                .where(FILTER_UTIL.findAllStringLikeIgnoreCase(componentFilter.getNameFilter(), Component_.name.getName()))
                .and(FILTER_UTIL.findAllStringLikeIgnoreCase(componentFilter.getDescriptionFilter(), Component_.description.getName()))
                .and(FILTER_UTIL.findAllIntegerGreaterThanOrEqualTo(componentFilter.getFromInStockFilter(), Component_.inStock.getName()))
                .and(FILTER_UTIL.findAllIntegerLessThanOrEqualTo(componentFilter.getToInStockFilter(), Component_.inStock.getName()))
                .and(FILTER_UTIL.findAllIntegerGreaterThanOrEqualTo(componentFilter.getFromMinInStockFilter(), Component_.minInStock.getName()))
                .and(FILTER_UTIL.findAllIntegerLessThanOrEqualTo(componentFilter.getToMinInStockFilter(), Component_.minInStock.getName()))
                .and(FILTER_UTIL.findAllDoubleGreaterThanOrEqualTo(componentFilter.getFromPriceFilter(), Component_.price.getName()))
                .and(FILTER_UTIL.findAllDoubleLessThanOrEqualTo(componentFilter.getToPriceFilter(), Component_.price.getName()))
                .and(FILTER_UTIL.findAllLocalDateTimeLessThanOrEqualTo(componentFilter.getToUpdatedFilter(), Component_.updated.getName()))
                .and(FILTER_UTIL.findAllLocalDateTimeGreaterThanOrEqualTo(componentFilter.getFromUpdatedFilter(), Component_.updated.getName()))
                .and(findAllSelectedProduct(componentFilter.getProductsFilter()))
                .and(findAllSelectedUsers(componentFilter.getUsersFilter()))
                .and(findAllTaggedAs(componentFilter.getTagFilter()));
    }

    private static Specification<Component> findAllTaggedAs(Enum<ComponentTag> tagFilter) {
        return switch (tagFilter) {
            case ComponentTag.IN_STOCK ->
                    (root, query, builder) -> builder.greaterThan(root.get(Component_.inStock), root.get(Component_.minInStock));
            case ComponentTag.SUPPLY -> (root, query, builder) -> builder.and(
                    builder.greaterThanOrEqualTo(root.get(Component_.inStock), 0),
                    builder.lessThanOrEqualTo(root.get(Component_.inStock), root.get(Component_.minInStock)));
            case ComponentTag.MISSING -> (root, query, builder) -> builder.lessThan(root.get(Component_.inStock), 0);
            default -> null;
        };
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
}
