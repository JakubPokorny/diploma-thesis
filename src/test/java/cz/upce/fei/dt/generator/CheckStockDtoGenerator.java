package cz.upce.fei.dt.generator;

import cz.upce.fei.dt.beckend.dto.CheckStockDto;
import cz.upce.fei.dt.beckend.entities.keys.ProductComponentKey;

import java.util.ArrayList;
import java.util.List;

public class CheckStockDtoGenerator {
    public static CheckStockDto generateCheckStockDto(Long productID, Long componentID) {
        return new CheckStockDto(
                productID,
                componentID,
                new ProductComponentKey(productID, componentID),
                "Component" + componentID,
                1,
                100,
                0,
                "email@email.com"
        );
    }
    public static List<CheckStockDto> generateCheckStockDto(List<Long> productIDs) {
        List<CheckStockDto> checkStockDtos = new ArrayList<>();
        productIDs.forEach(productID -> {
            for(long i = 0L; i < 3; i++){
                checkStockDtos.add(generateCheckStockDto(productID, i));
            }
        });
        return checkStockDtos;
    }
}
