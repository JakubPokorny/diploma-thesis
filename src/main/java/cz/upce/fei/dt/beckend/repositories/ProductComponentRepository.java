package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.entities.ProductComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductComponentRepository extends JpaRepository<ProductComponent, Long> {
    List<ProductComponent> findAllByProductId(Long id);

    List<ProductComponent> findAllByComponentId(Long componentID);
}
