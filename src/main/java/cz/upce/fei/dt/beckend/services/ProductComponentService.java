package cz.upce.fei.dt.beckend.services;

import cz.upce.fei.dt.beckend.entities.ProductComponent;
import cz.upce.fei.dt.beckend.repositories.ProductComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductComponentService {
    private final ProductComponentRepository productComponentRepository;

    public List<ProductComponent> getAll() {
        return productComponentRepository.findAll();
    }


    public List<ProductComponent> findAllByComponentId(Long componentID) {
        return productComponentRepository.findAllByComponentId(componentID);
    }

    public List<ProductComponent> findAllByProductId(Long productId) {
        return productComponentRepository.findAllByProductId(productId);
    }

    @Transactional
    public List<ProductComponent> saveAll(Iterable<ProductComponent> productComponents) {
        return productComponentRepository.saveAll(productComponents);
    }

    @Transactional
    public void deleteAll(Iterable<ProductComponent> productComponents) {
        productComponentRepository.deleteAll(productComponents);
    }
}
