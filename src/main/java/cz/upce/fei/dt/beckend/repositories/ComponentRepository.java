package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.entities.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long>, JpaSpecificationExecutor<Component> {
    @EntityGraph(value = "Component.eagerlyFetchProduct")
    @NonNull
    Page<Component> findAll(@Nullable Specification<Component> specification, @NonNull Pageable pageable);

// todo repair
//    @Query(value = """
//        SELECT
//        c.id as id, c.name as name, c.description as description, c.inStock as inStock, c.minInStock as minInStock, c.updated as updated,
//        pc.id as productComponentId, pc.amount as productComponentAmount,
//        u.id as userID, u.firstName as firstName, u.lastName as lastName,
//        p.id as productId, p.name as productName
//        FROM Component c
//        left JOIN fetch User u on c.user.id = u.id
//        left JOIN fetch ProductComponent pc on pc.id.componentId = c.id
//        left JOIN fetch Product p on p.id = pc.id.productId
//        """)
//    @NonNull
//    Page<IComponent> findAllIComponent(@Nullable Specification<Component> specification, @NonNull Pageable pageable);

    @Query(value = "select id, name from components where lower(name) like lower(concat('%', :searchTerm, '%'))", nativeQuery = true)
    @NonNull
    Page<cz.upce.fei.dt.beckend.dto.IComponent> findAllComponentsIDAndName(@NonNull Pageable pageable, @Param("searchTerm") String searchTerm);

    @Modifying
    @Transactional
    @Query(value = "update Component c set c.inStock = :inStock where c.id = :id")
    void updateAmountById(@NonNull @Param("id") Long id, @NonNull @Param("inStock") int inStock);
}