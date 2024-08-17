package cz.upce.fei.dt.backend.dto;

public record ComponentMetrics(
        int all,
        int inStock,
        int supply,
        int missing
) {
}
