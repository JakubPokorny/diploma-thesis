package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.entities.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    @EntityGraph(value = "Contract.eagerlyFetchProductsAndContactAndUser")
    @NonNull
    Page<Contract> findAll(@NonNull Pageable pageable);
}
