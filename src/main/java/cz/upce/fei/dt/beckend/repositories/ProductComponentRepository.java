package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.entities.ProductComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface ProductComponentRepository extends JpaRepository<ProductComponent, Long> {
    List<ProductComponent> findAllByProductId(Long id);

    List<ProductComponent> findAllByComponentId(Long componentID);
}
