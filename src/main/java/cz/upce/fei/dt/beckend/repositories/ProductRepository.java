package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.dto.ICheckProduct;
import cz.upce.fei.dt.beckend.dto.IProduct;
import cz.upce.fei.dt.beckend.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @EntityGraph(value = "Product.eagerlyFetchComponent")
    @NonNull
    Page<Product> findAll(@Nullable Specification<Product> specification, @NonNull Pageable pageable);

    @Query(value = "select id, name from products where lower(name) like lower(concat('%', :searchTerm, '%'))", nativeQuery = true)
    @NonNull
    Page<IProduct> findAllByName(@NonNull Pageable pageable, @Param("searchTerm") String searchTerm);

    @Query("""
            select
                pc.amount as componentPerProduct,
                c.id as componentId,
                c.name as componentName,
                c.inStock as componentsInStock,
                c.minInStock as minComponentsInStock,
                u.email as email
            from Product p
            join ProductComponent pc on pc.product.id = p.id
            left join Component c on c.id = pc.component.id
            left join User u on u.id = c.user.id
            where p.id = :productId
            """)
    Optional<List<ICheckProduct>> findByProductId(@Param("productId") Long productId);


}
