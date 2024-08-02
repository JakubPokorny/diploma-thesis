package cz.upce.fei.dt.beckend.dto;

public record ComponentMetrics(
        int all,
        int inStock,
        int supply,
        int missing
) {
}
