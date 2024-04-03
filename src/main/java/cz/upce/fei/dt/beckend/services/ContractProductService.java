package cz.upce.fei.dt.beckend.services;

import cz.upce.fei.dt.beckend.dto.CheckStockDto;
import cz.upce.fei.dt.beckend.entities.Contract;
import cz.upce.fei.dt.beckend.entities.ContractProduct;
import cz.upce.fei.dt.beckend.exceptions.StockException;
import cz.upce.fei.dt.beckend.repositories.ContractProductRepository;
import cz.upce.fei.dt.beckend.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class ContractProductService {
    private final ContractProductRepository contractProductRepository;
    private final ComponentService componentService;
    private final ProductRepository productRepository;

    @Transactional
    public void save(ContractProduct contractProduct) {
        contractProductRepository.save(contractProduct);
    }

    @Transactional
    public void saveAll(Set<ContractProduct> contractProducts) throws StockException {
        HashMap<Long, CheckStockDto> toUpdate = getComponentsToUpdate(contractProducts);
        HashMap<Long, CheckStockDto> missingComponents = getMissingComponents(toUpdate);
        if (missingComponents.isEmpty()) {
            contractProductRepository.saveAll(contractProducts);
            componentService.updateAllAmount(toUpdate.values());
        } else {
            throw new StockException(missingComponents.values(), "Objednávku nelze vytvořit, prootože některé komponenty nejsou skladem.");
        }
    }

    @Transactional
    public void deleteAllOrphans(Contract contract) {
        Set<ContractProduct> orphans = findAllOrphans(contract);
        HashMap<Long, CheckStockDto> toUpdate;
        if (!orphans.isEmpty()) {
            toUpdate = getComponentsToRelease(orphans);
            componentService.updateAllAmount(toUpdate.values());
            contractProductRepository.deleteAll(orphans);
        }
    }

    @Transactional
    public void deleteAll(Contract contract){
        HashMap<Long, CheckStockDto> toUpdate = getComponentsToRelease(contract.getContractProducts());
        if (!toUpdate.isEmpty()){
            componentService.updateAllAmount(toUpdate.values());
        }
        contractProductRepository.deleteAll(contract.getContractProducts());
    }
    private Set<ContractProduct> findAllOrphans(Contract contract){
        Set<ContractProduct> inDatabase = contractProductRepository.findAllByContractId(contract.getId());
        inDatabase.removeIf(contract.getContractProducts()::contains);
        return inDatabase;
    }

    private HashMap<Long, CheckStockDto> getComponentsToRelease(Set<ContractProduct> orphans) {
        HashMap<Long, CheckStockDto> toRelease = new HashMap<>();
        for (ContractProduct cp : orphans) {
            productRepository.findByProductId(cp.getProduct().getId())
                    .stream()
                    .map(iCheck -> CheckStockDto.builder()
                            .componentPerProduct(iCheck.getComponentPerProduct())
                            .componentId(iCheck.getComponentId())
                            .componentName(iCheck.getComponentName())
                            .componentsInStock(iCheck.getComponentsInStock())
                            .minComponentsInStock(iCheck.getMinComponentsInStock())
                            .email(iCheck.getEmail())
                            .build())
                    .forEach(checker -> {
                        int componentsForContract = cp.getAmount() * checker.getComponentPerProduct();
                        int leftInStock;
                        Long componentId = checker.getComponentId();

                        if (toRelease.containsKey(componentId)) {
                            leftInStock = toRelease.get(componentId).getComponentsInStock() + componentsForContract;
                            toRelease.get(componentId).setComponentsInStock(leftInStock);
                        } else {
                            leftInStock = checker.getComponentsInStock() + componentsForContract;
                            checker.setComponentsInStock(leftInStock);
                            toRelease.put(componentId, checker);
                        }
                    });
        }
        return toRelease;
    }

    private HashMap<Long, CheckStockDto> getMissingComponents(HashMap<Long, CheckStockDto> toUpdate) {
        HashMap<Long, CheckStockDto> missingComponents = new HashMap<>();
        toUpdate.values().forEach(productDTO -> {
            if (productDTO.getComponentsInStock() < 0) {
                missingComponents.put(productDTO.getComponentId(), productDTO);
            }
        });
        return missingComponents;
    }

    private HashMap<Long, CheckStockDto> getComponentsToUpdate(Set<ContractProduct> contractProducts) {
        HashMap<Long, CheckStockDto> toUpdate = new HashMap<>();

        for (ContractProduct cpToSave : contractProducts) {
            int orderedProducts;
            Optional<ContractProduct> cpInDatabase = contractProductRepository.findById(cpToSave.getId());
            if (cpInDatabase.isPresent()) {
                if (cpInDatabase.get().getAmount() == cpToSave.getAmount()) {
                    continue;
                } else {
                    orderedProducts = cpToSave.getAmount() - cpInDatabase.get().getAmount();
                }
            } else {
                orderedProducts = cpToSave.getAmount();
            }

            productRepository.findByProductId(cpToSave.getProduct().getId())
                    .stream()
                    .map(iCheck -> CheckStockDto.builder()
                            .componentPerProduct(iCheck.getComponentPerProduct())
                            .componentId(iCheck.getComponentId())
                            .componentName(iCheck.getComponentName())
                            .componentsInStock(iCheck.getComponentsInStock())
                            .minComponentsInStock(iCheck.getMinComponentsInStock())
                            .email(iCheck.getEmail())
                            .build())
                    .forEach(checker -> {
                        int componentsForContract = orderedProducts * checker.getComponentPerProduct();
                        int leftInStock;
                        Long componentId = checker.getComponentId();

                        if (toUpdate.containsKey(componentId)) {
                            leftInStock = toUpdate.get(componentId).getComponentsInStock() - componentsForContract;
                            toUpdate.get(componentId).setComponentsInStock(leftInStock);
                        } else {
                            leftInStock = checker.getComponentsInStock() - componentsForContract;
                            checker.setComponentsInStock(leftInStock);
                            toUpdate.put(componentId, checker);
                        }
                    });
        }
        return toUpdate;
    }

}
