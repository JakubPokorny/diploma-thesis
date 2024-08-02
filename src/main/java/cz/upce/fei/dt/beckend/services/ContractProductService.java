package cz.upce.fei.dt.beckend.services;

import cz.upce.fei.dt.beckend.dto.CheckStockDto;
import cz.upce.fei.dt.beckend.entities.Contract;
import cz.upce.fei.dt.beckend.entities.ContractProduct;
import cz.upce.fei.dt.beckend.exceptions.StockException;
import cz.upce.fei.dt.beckend.repositories.ContractProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.IntBinaryOperator;

@Service
@AllArgsConstructor
public class ContractProductService {
    private final ContractProductRepository contractProductRepository;
    private final ComponentService componentService;
    private final ProductService productService;

    @Transactional
    public void save(ContractProduct contractProduct) {
        contractProductRepository.save(contractProduct);
    }

    @Transactional
    public void saveAll(Contract contract) {
        HashMap<Long, CheckStockDto> componentsToUpdate = getComponentsToUpdate(contract);
        List<CheckStockDto> missingComponents = getMissingComponents(componentsToUpdate);

        contractProductRepository.saveAll(contract.getContractProducts());
        componentService.updateAll(componentsToUpdate.values());

        if (!missingComponents.isEmpty())
            new StockException(missingComponents, "Chybí komponenty").showNotification();
    }

    @Transactional
    public void deleteAll(Contract contract) {
        HashMap<Long, CheckStockDto> toUpdate = updateHashMap(new HashMap<>(), contract.getContractProducts(), null, Integer::sum);
        if (!toUpdate.isEmpty()) {
            componentService.updateAll(toUpdate.values());
        }
        contractProductRepository.deleteAll(contract.getContractProducts());
    }

    private HashMap<Long, CheckStockDto> getComponentsToUpdate(Contract contract) {
        HashMap<Long, CheckStockDto> componentsToUpdate = new HashMap<>();

        HashMap<Long, ContractProduct> persisted = new HashMap<>();
        contractProductRepository
                .findAllByContractId(contract.getId())
                .forEach(contractProduct -> persisted.put(contractProduct.getId().getProductId(), contractProduct));

        updateHashMap(componentsToUpdate, contract.getContractProducts(), persisted, (a, b) -> a - b);

        Set<ContractProduct> orphans = new HashSet<>(persisted.values());
        updateHashMap(componentsToUpdate, orphans, null, Integer::sum);
        return componentsToUpdate;
    }


    private List<CheckStockDto> getMissingComponents(HashMap<Long, CheckStockDto> componentsToUpdate) {
        List<CheckStockDto> missingComponents = new ArrayList<>();
        componentsToUpdate.values().forEach(checkStockDto -> {
            if (checkStockDto.getComponentsInStock() < 0) {
                missingComponents.add(checkStockDto);
            }
        });
        return missingComponents;
    }

    private HashMap<Long, CheckStockDto> updateHashMap(HashMap<Long, CheckStockDto> hashMap, Set<ContractProduct> contractProducts, HashMap<Long, ContractProduct> persisted, IntBinaryOperator operator) {
        Iterable<Long> productsID = contractProducts.stream().map(cp -> cp.getId().getProductId()).toList();
        List<CheckStockDto> checkStockDtos = productService.findAllByID(productsID);

        for (ContractProduct CP : contractProducts) {
            int orderedProducts;
            if (persisted != null && !persisted.isEmpty()) {
                Long productID = CP.getId().getProductId();
                ContractProduct persistedCP = persisted.get(productID);
                if (persistedCP != null) {
                    persisted.remove(productID);
                    if (persistedCP.getAmount() == CP.getAmount()) {
                        continue;
                    } else {
                        orderedProducts = CP.getAmount() - persistedCP.getAmount();
                    }
                } else {
                    orderedProducts = CP.getAmount();
                }
            } else {
                orderedProducts = CP.getAmount();
            }

            List<CheckStockDto> filtered = checkStockDtos.stream().filter(checkStockDto -> checkStockDto.getProductID().equals(CP.getId().getProductId())).toList();
            filtered.forEach(checkStockDto -> {
                int componentsForContract = orderedProducts * checkStockDto.getComponentsPerProduct();
                Long componentId = checkStockDto.getComponentID();

                if (hashMap.containsKey(componentId)) {
                    CheckStockDto componentToUpdate = hashMap.get(componentId);
                    componentToUpdate.setComponentsInStock(operator.applyAsInt(componentToUpdate.getComponentsInStock(), componentsForContract));
                } else {
                    checkStockDto.setComponentsInStock(operator.applyAsInt(checkStockDto.getComponentsInStock(), componentsForContract));
                    hashMap.put(componentId, checkStockDto);
                }
            });
        }
        return hashMap;
    }
}
