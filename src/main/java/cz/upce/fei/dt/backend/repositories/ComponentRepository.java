package cz.upce.fei.dt.backend.repositories;

import cz.upce.fei.dt.backend.dto.IComponent;
import cz.upce.fei.dt.backend.dto.IComponentCount;
import cz.upce.fei.dt.backend.entities.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface ComponentRepository extends JpaRepository<Component, Long>, JpaSpecificationExecutor<Component> {
    @EntityGraph(value = "Component.eagerlyFetchProduct")
    @NonNull
    List<Component> findAll(@Nullable Specification<Component> specification, @NonNull Sort sort);

    @Query(value = "select id, name, price from components where lower(name) like lower(concat('%', :searchTerm, '%'))",
            nativeQuery = true)
    @NonNull
    Page<IComponent> findAllByName(@NonNull Pageable pageable, @Param("searchTerm") String searchTerm);

    @Modifying
    @Transactional
    @Query(value = "update Component c set c.inStock = :inStock where c.id = :id")
    void updateAmountById(@NonNull @Param("id") Long id, @NonNull @Param("inStock") int inStock);

    @Modifying
    @Transactional
    @Query(value = "update Component c set c.user.id = :alternateUserId where c.user.id = :userId")
    void updateAllUserByUser(@NonNull @Param("userId") Long userId, @NonNull @Param("alternateUserId") Long alternateUserId);

    @Query(value = "select inStock as inStock, minInStock as minInStock from Component ")
    List<IComponentCount> findAllComponentMetrics();
}