package cz.upce.fei.dt.beckend.dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CheckStockDto {
    private int componentPerProduct;
    private Long componentId;
    private String componentName;
    private int componentsInStock;
    private Integer minComponentsInStock;
    private String email;
}
