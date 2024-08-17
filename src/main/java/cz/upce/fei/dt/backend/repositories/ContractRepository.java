package cz.upce.fei.dt.backend.repositories;

import cz.upce.fei.dt.backend.entities.Contract;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {

    @EntityGraph(value = "Contract.eagerlyFetchProductsAndContactAndUser")
    @NonNull
    List<Contract> findAll(@Nullable Specification<Contract> specification, @NonNull Sort sort);

    @Query(value = "select count(id) from Contract")
    int countAll();
}
