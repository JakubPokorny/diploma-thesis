package cz.upce.fei.dt.backend.repositories;

import cz.upce.fei.dt.backend.dto.ICheckExpiredFinalDeadline;
import cz.upce.fei.dt.backend.entities.Contract;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {

    @EntityGraph(value = "Contract.eagerlyFetchProductsAndContactAndUser")
    @NonNull
    List<Contract> findAll(@Nullable Specification<Contract> specification, @NonNull Sort sort);

    @Query(value = "select count(id) from Contract")
    int countAll();

    @Query(value = """
            select
            c.id as id,
            c.price as price,
            c.final_deadline as finalDeadline,
            GROUP_CONCAT(p.name SEPARATOR ', ') as orderedProducts,
            u.email as email
            from contracts c
            join users u on c.user_id = u.id
            join contract_products cp on c.id = cp.contract_id
            join products p on cp.product_id = p.id
            where c.final_deadline < now()
            group by c.id, c.price, c.final_deadline, u.email
            """, nativeQuery = true)
    List<ICheckExpiredFinalDeadline> findAllByExpiredFinalDeadline();

    @Modifying
    @Transactional
    @Query(value = "update Contract c set c.createdBy.id = :alternateUserId where c.createdBy.id = :userId")
    void updateAllUserByUser(@NonNull @Param("userId") Long userId, @NonNull @Param("alternateUserId") Long alternateUserId);
}
