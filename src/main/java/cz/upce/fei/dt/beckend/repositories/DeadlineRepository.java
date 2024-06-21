package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.dto.IDeadline;
import cz.upce.fei.dt.beckend.entities.Deadline;
import cz.upce.fei.dt.beckend.entities.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeadlineRepository extends JpaRepository<Deadline, Long> {

    @Query("select d from Deadline d where d.contract.id = :contractId")
    @NonNull
    Page<IDeadline> findAllByContractId(@Param("contractId") Long contractId, @NonNull Pageable pageable);

    Optional<Deadline> findFirstByContractIdOrderByCreatedDesc(Long contractId);

    void deleteAllByContractId(Long contractId);

    @Query(value = """
            SELECT d
            FROM Deadline d
                     INNER JOIN (
                SELECT d.contract.id AS contract_id, MAX(d.created) AS latest_created
                FROM Deadline d
                GROUP BY d.contract.id
            ) sub ON d.contract.id = sub.contract_id AND d.created = sub.latest_created
            join Status s on s.id = d.status.id
            where s.theme = :theme
            """)
    List<Deadline> findAllCurrentDeadlinesByStatus(@NonNull @Param("theme") Status.Theme theme);

    @Query(value = """
            SELECT d
            FROM Deadline d
                     INNER JOIN (
                SELECT d.contract.id AS contract_id, MAX(d.created) AS latest_created
                FROM Deadline d
                GROUP BY d.contract.id
            ) sub ON d.contract.id = sub.contract_id AND d.created = sub.latest_created
            """)
    List<Deadline> findAllCurrentDeadlines();

    @Query(value =
        """
        SELECT count(d.contract_id)
        FROM deadlines d
            INNER JOIN (
            SELECT contract_id, MAX(created) AS latest_created
            FROM deadlines
            GROUP BY contract_id
        ) sub ON d.contract_id = sub.contract_id AND d.created = sub.latest_created
        WHERE deadline IS NOT NULL AND deadline < DATE(now())
        """
            , nativeQuery = true)
    int countAfterDeadline();

    @Query(value =
            """
            SELECT count(d.contract_id)
            FROM deadlines d
                INNER JOIN (
                SELECT contract_id, MAX(created) AS latest_created
                FROM deadlines
                GROUP BY contract_id
            ) sub ON d.contract_id = sub.contract_id AND d.created = sub.latest_created
            WHERE deadline IS NULL
            """
            , nativeQuery = true)
    int countWithoutDeadline();

    @Query(value =
            """
            SELECT count(d.contract_id)
            FROM deadlines d
                INNER JOIN (
                SELECT contract_id, MAX(created) AS latest_created
                FROM deadlines
                GROUP BY contract_id
            ) sub ON d.contract_id = sub.contract_id AND d.created = sub.latest_created
            WHERE deadline IS NOT NULL AND DATE(now()) <= deadline
            """
            , nativeQuery = true)
    int countBeforeDeadline();
}
