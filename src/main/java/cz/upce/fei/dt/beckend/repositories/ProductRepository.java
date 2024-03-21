package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.dto.IProduct;
import cz.upce.fei.dt.beckend.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @EntityGraph(value = "Product.eagerlyFetchComponent")
    @NonNull Page<Product> findAll(@NonNull Pageable pageable);

    @Query(value = "select id, name from products", nativeQuery = true)
    List<IProduct> findAllProductsIdAndName();
}
