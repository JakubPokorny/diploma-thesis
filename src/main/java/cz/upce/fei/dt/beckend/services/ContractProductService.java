package cz.upce.fei.dt.beckend.services;

import cz.upce.fei.dt.beckend.entities.ContractProduct;
import cz.upce.fei.dt.beckend.repositories.ContractProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ContractProductService {
    private final ContractProductRepository contractProductRepository;

    @Transactional
    public void save(ContractProduct contractProduct) {
        contractProductRepository.save(contractProduct);
    }
}
