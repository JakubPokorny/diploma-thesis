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
    @Transactional
    public List<ProductComponent> getAll(){
        return productComponentRepository.findAll();
    }

    public void saveProductComponent(ProductComponent productComponent){
        productComponentRepository.save(productComponent);
    }
}
