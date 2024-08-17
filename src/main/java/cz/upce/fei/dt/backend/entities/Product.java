package cz.upce.fei.dt.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
@NamedEntityGraph(
        name = "Product.eagerlyFetchComponent",
        attributeNodes = {
                @NamedAttributeNode(value = "productComponents", subgraph = "productComponentSubgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "productComponentSubgraph",
                        attributeNodes = {@NamedAttributeNode("component")}
                )
        }
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Double productionPrice = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double profit = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double sellingPrice = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ownSellingPrice = false;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime created;

    @Column
    @UpdateTimestamp
    private LocalDateTime updated;

    @OneToMany(mappedBy = "product", cascade = CascadeType.MERGE, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
    private Set<ProductComponent> productComponents = new HashSet<>();

    public List<Component> getSelectedComponents() {
        List<Component> selectedComponents = new ArrayList<>();
        productComponents.forEach(productComponent ->
                selectedComponents.add(productComponent.getComponent())
        );
        return selectedComponents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
