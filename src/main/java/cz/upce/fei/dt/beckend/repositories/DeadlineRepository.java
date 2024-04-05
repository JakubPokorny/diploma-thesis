package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.dto.IDeadline;
import cz.upce.fei.dt.beckend.entities.Deadline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface DeadlineRepository extends JpaRepository<Deadline, Long> {

    @Query("select d from Deadline d where d.contract.id = :contractId")
    @NonNull
    Page<IDeadline> findAllByContractId(@Param("contractId") Long contractId, @NonNull Pageable pageable);
}
