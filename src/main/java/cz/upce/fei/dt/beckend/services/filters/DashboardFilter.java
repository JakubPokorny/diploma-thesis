package cz.upce.fei.dt.beckend.services.filters;

import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class DashboardFilter {
    private LocalDateTime fromDateTime;
    private LocalDateTime toDateTime;
    private Set<Product> productsFilter = Collections.emptySet();
    private Set<Status.Theme> statusFilter = Collections.emptySet();

    public void setFromDateTime(LocalDate fromDate) {
        this.fromDateTime = fromDate.atTime(0, 0);
    }

    public void setToDateTime(LocalDate toDateTime) {
        this.toDateTime = toDateTime.atTime(23, 59);
    }

    public LocalDate getFromDate() {
        return fromDateTime == null ? null : fromDateTime.toLocalDate();
    }

    public LocalDate getToDate() {
        return toDateTime == null ? null : toDateTime.toLocalDate();
    }

    public Set<Long> getProductIDs(){
        return productsFilter.stream().map(Product::getId).collect(Collectors.toSet());
    }
}
