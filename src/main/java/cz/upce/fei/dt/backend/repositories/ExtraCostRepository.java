package cz.upce.fei.dt.backend.repositories;

import cz.upce.fei.dt.backend.dto.IExtraCost;
import cz.upce.fei.dt.backend.entities.ExtraCost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ExtraCostRepository extends JpaRepository<ExtraCost, Long> {
    @Query(value = "select count(id) as count, sum(extraCost) as totalExtraCost from ExtraCost where contract.id = :contractId")
    IExtraCost countByContractId(@Param("contractId") Long contractId);

    @NonNull
    Page<ExtraCost> findAllByContractId(@NonNull Pageable pageable, @NonNull Long contractId);

    @Modifying
    @Transactional
    @Query(value = "update ExtraCost ec set ec.createdBy.id = :alternateUserId where ec.createdBy.id = :userId")
    void updateAllCreatedByUser(@NonNull @Param("userId") Long userId, @NonNull @Param("alternateUserId") Long alternateUserId);

    @Modifying
    @Transactional
    @Query(value = "update ExtraCost ec set ec.updatedBy.id = :alternateUserId where ec.updatedBy.id = :userId")
    void updateAllUpdatedByUser(@NonNull @Param("userId") Long userId, @NonNull @Param("alternateUserId") Long alternateUserId);
}
