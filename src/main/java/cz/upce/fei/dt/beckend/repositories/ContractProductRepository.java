package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.entities.ContractProduct;
import cz.upce.fei.dt.beckend.entities.keys.ContractProductKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractProductRepository extends JpaRepository<ContractProduct, ContractProductKey> {
    List<ContractProduct> findAllByContractId(Long id);
}
