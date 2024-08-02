package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.dto.ICheckProduct;
import cz.upce.fei.dt.beckend.dto.IMostSaleableProducts;
import cz.upce.fei.dt.beckend.dto.IProduct;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.Status;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @EntityGraph(value = "Product.eagerlyFetchComponent")
    @NonNull
    List<Product> findAll(@Nullable Specification<Product> specification, @NonNull Sort sort);

    @Query(value = "select id, name, selling_price as sellingPrice from products where lower(name) like lower(concat('%', :searchTerm, '%'))", nativeQuery = true)
    @NonNull
    Page<IProduct> findAllByName(@NonNull Pageable pageable, @Param("searchTerm") String searchTerm);

    @Query("""
        select p
        from Product p
        join ProductComponent pc on pc.id.productId = p.id
        where pc.id.componentId = :componentId
        """)
    List<Product> findAllByComponentId(@NonNull @Param("componentId") Long componentId);

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
    List<ICheckProduct> findAllByID(@NonNull @Param("productIDs") Iterable<Long> productIDs);

    @EntityGraph(value = "Product.eagerlyFetchComponent")
    @NonNull
    List<Product> findAllById(@NonNull @Param("productIDs") Iterable<Long> productIDs);

    @Query("""
            select
                p.name as name,
                sum(cp.amount) as amount
            from Deadline d
            inner join (
                select d.contract.id as contract_id, max(d.created) as latest_created
                from Deadline d
                group by d.contract.id
            ) sub on sub.contract_id = d.contract.id AND d.created = sub.latest_created
            join Status s on s.id = d.status.id
            join Contract c on c.id = d.contract.id
            join ContractProduct cp on cp.id.contractId = c.id
            join Product p on p.id = cp.id.productId
            where p.id in :productIDs
            and s.theme in :themes
            and d.updated >= :from
            and d.updated <= :to
            group by p.id
            """)
    List<IMostSaleableProducts> getMostSaleableProducts(
            @Param("productIDs") Set<Long> productIDs,
            @Param("themes") Set<Status.Theme> themes,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
            );
}
