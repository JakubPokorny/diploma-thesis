package cz.upce.fei.dt.backend.repositories;

import cz.upce.fei.dt.backend.dto.ICheckExpiredPartialDeadline;
import cz.upce.fei.dt.backend.dto.IDeadline;
import cz.upce.fei.dt.backend.entities.Deadline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeadlineRepository extends JpaRepository<Deadline, Long> {

    @Query("""
            select
            d.id as id,
            d.status as status,
            d.deadline as deadline,
            d.created as created,
            d.user.firstName as firstName,
            d.user.lastName as lastName
            from Deadline d where d.contract.id = :contractId
            """)
    @NonNull
    Page<IDeadline> findAllByContractId(@NonNull Pageable pageable, @NonNull @Param("contractId") Long contractId);

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
            """)
    List<Deadline> findAllCurrentDeadlines();

    @Query(value = """
            SELECT
                u.email as email,
                c.id as id,
                c.price as price,
                s.status as status,
                d.deadline as partialDeadline,
                GROUP_CONCAT(p.name SEPARATOR ', ') as orderedProducts
            FROM deadlines d
                     INNER JOIN (
                SELECT d.contract_id AS contract_id, MAX(d.created) AS latest_created
                FROM deadlines d
                where d.deadline < now()
                GROUP BY d.contract_id
            ) sub ON d.contract_id = sub.contract_id AND d.created = sub.latest_created
            join users u on d.user_id = u.id
            join contracts c on d.contract_id = c.id
            join statuses s on d.status_id = s.id
            join contract_products cp on  cp.contract_id = c.id
            join products p on cp.product_id = p.id
            group by d.deadline, c.id, c.price, s.status, u.email
            """, nativeQuery = true)
    List<ICheckExpiredPartialDeadline> findAllExpiredCurrentPartialDeadlines();

    @Query(value = """
            SELECT d
            FROM Deadline d
                     INNER JOIN (
                SELECT d.contract.id AS contract_id, MAX(d.created) AS latest_created
                FROM Deadline d
                GROUP BY d.contract.id
            ) sub ON d.contract.id = sub.contract_id AND d.created = sub.latest_created
            where d.status.id = :statusId
            """)
    List<Deadline> findAllCurrentDeadlinesByStatusId(@NonNull @Param("statusId") Long statusId);


    @Modifying
    @Transactional
    @Query(value = "update Deadline d set d.user.id = :alternateUserId where d.user.id = :userId")
    void updateAllUserByUser(@NonNull @Param("userId") Long userId, @NonNull @Param("alternateUserId") Long alternateUserId);
}
