package cz.upce.fei.dt.beckend.dto;


import cz.upce.fei.dt.beckend.entities.keys.ProductComponentKey;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CheckStockDto {
    private Long productID;
    private Long componentID;
    private ProductComponentKey productComponentKey;
    private String componentName;
    private int componentsPerProduct;
    private int componentsInStock;
    private Integer minComponentsInStock;
    private String email;

    public static CheckStockDto toCheckStockDTO(ICheckProduct iCheckProduct){
        return CheckStockDto.builder()
                .componentID(iCheckProduct.getComponentID())
                .productID(iCheckProduct.getProductID())
                .productComponentKey(iCheckProduct.getProductComponentKey())
                .componentsPerProduct(iCheckProduct.getComponentsPerProduct())
                .componentName(iCheckProduct.getComponentName())
                .componentsInStock(iCheckProduct.getComponentsInStock())
                .minComponentsInStock(iCheckProduct.getMinComponentsInStock())
                .email(iCheckProduct.getEmail())
                .build();
    }
}
