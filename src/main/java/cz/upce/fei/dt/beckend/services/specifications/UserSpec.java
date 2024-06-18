package cz.upce.fei.dt.beckend.services.specifications;

import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.entities.User_;
import cz.upce.fei.dt.beckend.services.filters.UserFilter;
import org.springframework.data.jpa.domain.Specification;

public class UserSpec {
    private static final FilterUtil<User> FILTER_UTIL = new FilterUtil<>();

    public static Specification<User> filterBy(UserFilter userFilter){
        if (userFilter == null)
            return Specification.where(null);
        return Specification
                .where(FILTER_UTIL.findAllStringLikeIgnoreCase(userFilter.getFirstNameFilter(), User_.FIRST_NAME))
                .and(FILTER_UTIL.findAllStringLikeIgnoreCase(userFilter.getLastNameFilter(), User_.LAST_NAME))
                .and(FILTER_UTIL.findAllStringLikeIgnoreCase(userFilter.getEmailFilter(), User_.EMAIL))
                .and(FILTER_UTIL.findAllStringLikeIgnoreCase(userFilter.getRoleFilter(), User_.ROLE))
                .and(FILTER_UTIL.findAllStringLikeIgnoreCase(userFilter.getTokenFilter(), User_.RESET_TOKEN));
    }
}
