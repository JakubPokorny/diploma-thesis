package cz.upce.fei.dt.beckend.services.specifications;

import cz.upce.fei.dt.beckend.entities.Status;
import cz.upce.fei.dt.beckend.entities.Status_;
import cz.upce.fei.dt.beckend.services.filters.StatusFilter;
import org.springframework.data.jpa.domain.Specification;

public class StatusSpec {
    private static final FilterUtil<Status> FILTER_UTIL = new FilterUtil<>();

    public static Specification<Status> filterBy(StatusFilter statusFilter){
        if (statusFilter == null)
            return Specification.where(null);
        return Specification
                .where(FILTER_UTIL.findAllStringLikeIgnoreCase(statusFilter.getStatusFilter(), Status_.STATUS))
                .and(FILTER_UTIL.findAllStringLikeIgnoreCase(statusFilter.getThemeFilter(), Status_.THEME));
    }
}
