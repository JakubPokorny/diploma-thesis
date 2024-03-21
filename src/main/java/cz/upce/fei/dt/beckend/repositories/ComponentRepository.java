package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.dto.IComponent;
import cz.upce.fei.dt.beckend.entities.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {
    @EntityGraph(value = "Component.eagerlyFetchProduct")
    @NonNull Page<Component> findAll(@NonNull Pageable pageable);

    @Query(value = "select id, name from components", nativeQuery = true)
    List<IComponent> findAllComponentsIDAndName();
}