package cz.upce.fei.dt.beckend.dto;

import java.time.LocalDateTime;

public interface IProduct {
    Long getId();

    String getName();
    Double getProductionPrice();
    Double getProfit();
    Double getSellingPrice();
    Boolean getOwnSellingPrice();
    LocalDateTime getCreated();
    LocalDateTime getUpdated();

}
