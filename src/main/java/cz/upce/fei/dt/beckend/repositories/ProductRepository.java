package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.dto.ICheckProduct;
import cz.upce.fei.dt.beckend.dto.IProduct;
import cz.upce.fei.dt.beckend.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @EntityGraph(value = "Product.eagerlyFetchComponent")
    @NonNull
    List<Product> findAll(@Nullable Specification<Product> specification, @NonNull Sort sort);

    @Query(value = "select id, name, selling_price as sellingPrice from products where lower(name) like lower(concat('%', :searchTerm, '%'))", nativeQuery = true)
    @NonNull
    Page<IProduct> findAllByName(@NonNull Pageable pageable, @Param("searchTerm") String searchTerm);

    @Query("""
            select
                p.id as productID,
                pc.componentsPerProduct as componentsPerProduct,
                pc.id as productComponentKey,
                c.id as componentID,
                c.name as componentName,
                c.inStock as componentsInStock,
                c.minInStock as minComponentsInStock,
                u.email as email
            from Product p
            join ProductComponent pc on pc.product.id = p.id
            left join Component c on c.id = pc.component.id
            left join User u on u.id = c.user.id
            where p.id in :productIDs
            """)
    List<ICheckProduct> findAllByID(@Param("productIDs") Iterable<Long> productIDs);

    @EntityGraph(value = "Product.eagerlyFetchComponent")
    @NonNull
    List<Product> findAllById(@NonNull @Param("productIDs") Iterable<Long> productIDs);
}
