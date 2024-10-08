package cz.upce.fei.dt.backend.services;

import cz.upce.fei.dt.backend.entities.ProductComponent;
import cz.upce.fei.dt.backend.repositories.ProductComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductComponentService {
    private final ProductComponentRepository productComponentRepository;

    public List<ProductComponent> findAllByComponentId(Long componentId) {
        return productComponentRepository.findAllByComponentId(componentId);
    }

    public List<ProductComponent> findAllByProductId(Long productId) {
        return productComponentRepository.findAllByProductId(productId);
    }

    @Transactional
    public void deleteAll(Iterable<ProductComponent> productComponents) {
        productComponentRepository.deleteAll(productComponents);
    }
}
