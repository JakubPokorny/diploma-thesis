package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.dto.IComponent;
import cz.upce.fei.dt.beckend.entities.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {
    @EntityGraph(value = "Component.eagerlyFetchProduct")
    @NonNull Page<Component> findAll(@NonNull Pageable pageable);

    @Query(value = "select id, name from components where lower(name) like lower(concat('%', :searchTerm, '%'))", nativeQuery = true)
    @NonNull
    Page<IComponent> findAllComponentsIDAndName(@NonNull Pageable pageable, @Param("searchTerm") String searchTerm);

    @Modifying
    @Transactional
    @Query(value = "update Component c set c.amount = :amount where c.id = :id")
    void updateAmountById(@NonNull @Param("id") Long id, @NonNull @Param("amount") int amount);
}