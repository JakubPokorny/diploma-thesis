package cz.upce.fei.dt.backend.dto;


import cz.upce.fei.dt.backend.entities.keys.ProductComponentKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class CheckStockDto {
    private Long productID;
    private Long componentID;
    private ProductComponentKey productComponentKey;
    private String componentName;
    private int componentsPerProduct;
    private int componentsInStock;
    private int minComponentsInStock;
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

    public boolean sendNotification(){
        return email != null && componentsInStock < minComponentsInStock;
    }
}
