package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.entities.ContractProduct;
import cz.upce.fei.dt.beckend.entities.keys.ContractProductKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ContractProductRepository extends JpaRepository<ContractProduct, ContractProductKey> {
    Set<ContractProduct> findAllByContractId(Long id);

    @NonNull
    Optional<ContractProduct> findById(@NonNull ContractProductKey id);

}
