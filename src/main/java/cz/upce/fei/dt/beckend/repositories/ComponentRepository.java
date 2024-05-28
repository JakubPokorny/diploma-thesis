package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.dto.IComponent;
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

    @Query(value = "select id, name from components where lower(name) like lower(concat('%', :searchTerm, '%'))", nativeQuery = true)
    @NonNull
    Page<IComponent> findAllByName(@NonNull Pageable pageable, @Param("searchTerm") String searchTerm);

    @Modifying
    @Transactional
    @Query(value = "update Component c set c.inStock = :inStock where c.id = :id")
    void updateAmountById(@NonNull @Param("id") Long id, @NonNull @Param("inStock") int inStock);

    @Query(value = "select count(id) from Component")
    int countAll();
    @Query(value = "select count(id) from Component where inStock > minInStock")
    int countInStock();
    @Query(value = "select count(id) from Component where inStock < 0")
    int countMissing();
    @Query(value = "select count(id) from Component where inStock <= minInStock and inStock >= 0")
    int countSupply();
}