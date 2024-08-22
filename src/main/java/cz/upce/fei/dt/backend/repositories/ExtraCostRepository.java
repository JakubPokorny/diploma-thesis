package cz.upce.fei.dt.backend.repositories;

import cz.upce.fei.dt.backend.dto.IExtraCost;
import cz.upce.fei.dt.backend.entities.ExtraCost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtraCostRepository extends JpaRepository<ExtraCost, Long> {
    @Query(value = "select count(id) as count, sum(extraCost) as totalExtraCost from ExtraCost where contract.id = :contractId")
    IExtraCost countByContractId(@Param("contractId") Long contractId);

    @NonNull
    Page<ExtraCost> findAllByContractId(@NonNull Pageable pageable, @NonNull Long contractId);
}
