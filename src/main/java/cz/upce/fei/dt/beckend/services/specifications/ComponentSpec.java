package cz.upce.fei.dt.beckend.services.specifications;

import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.Component_;
import cz.upce.fei.dt.beckend.services.filters.ComponentFilter;
import cz.upce.fei.dt.beckend.services.filters.ComponentTag;
import org.springframework.data.jpa.domain.Specification;

public class ComponentSpec {
    private static final FilterUtil<Component> FILTER_UTIL = new FilterUtil<>();

    public static Specification<Component> filterBy(ComponentFilter componentFilter) {
        return Specification
                .where(FILTER_UTIL.findAllStringLikeIgnoreCase(componentFilter.getNameFilter(), Component_.NAME))
                .and(FILTER_UTIL.findAllStringLikeIgnoreCase(componentFilter.getDescriptionFilter(), Component_.DESCRIPTION))
                .and(FILTER_UTIL.findAllIntegerGreaterThanOrEqualTo(componentFilter.getFromInStockFilter(), Component_.IN_STOCK))
                .and(FILTER_UTIL.findAllIntegerLessThanOrEqualTo(componentFilter.getToInStockFilter(), Component_.IN_STOCK))
                .and(FILTER_UTIL.findAllIntegerGreaterThanOrEqualTo(componentFilter.getFromMinInStockFilter(), Component_.MIN_IN_STOCK))
                .and(FILTER_UTIL.findAllIntegerLessThanOrEqualTo(componentFilter.getToMinInStockFilter(), Component_.MIN_IN_STOCK))
                .and(FILTER_UTIL.findAllDoubleGreaterThanOrEqualTo(componentFilter.getFromPriceFilter(), Component_.PRICE))
                .and(FILTER_UTIL.findAllDoubleLessThanOrEqualTo(componentFilter.getToPriceFilter(), Component_.PRICE))
                .and(FILTER_UTIL.findAllLocalDateLessThanOrEqualTo(componentFilter.getFromCreatedFilter(), Component_.CREATED))
                .and(FILTER_UTIL.findAllLocalDateGreaterThanOrEqualTo(componentFilter.getToCreatedFilter(), Component_.CREATED))
                .and(FILTER_UTIL.findAllLocalDateLessThanOrEqualTo(componentFilter.getToUpdatedFilter(), Component_.UPDATED))
                .and(FILTER_UTIL.findAllLocalDateGreaterThanOrEqualTo(componentFilter.getFromUpdatedFilter(), Component_.UPDATED))
                .and(findAllTaggedAs(componentFilter.getTagFilter()));
    }

    private static Specification<Component> findAllTaggedAs(Enum<ComponentTag> tagFilter) {
        return switch (tagFilter) {
            case ComponentTag.WITHOUT_LIMIT -> ((root, _, builder) -> builder.isNull(root.get(Component_.minInStock)));
            case ComponentTag.IN_STOCK ->
                    (root, _, builder) -> builder.greaterThan(root.get(Component_.inStock), root.get(Component_.minInStock));
            case ComponentTag.SUPPLY -> (root, _, builder) -> builder.and(
                    builder.greaterThanOrEqualTo(root.get(Component_.inStock), 0),
                    builder.lessThanOrEqualTo(root.get(Component_.inStock), root.get(Component_.minInStock)));
            case ComponentTag.MISSING -> (root, _, builder) -> builder.lessThan(root.get(Component_.inStock), 0);
            default -> null;
        };
    }
}
