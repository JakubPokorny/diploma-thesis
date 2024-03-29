package cz.upce.fei.dt.beckend.entities;

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
    @CreationTimestamp
    private LocalDateTime created;

    @Column
    @UpdateTimestamp
    private LocalDateTime updated;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private Set<ProductComponent> productComponents = new HashSet<>();

    public List<Component> getSelectedComponents() {
        List<Component> selectedComponents = new ArrayList<>();
        productComponents.forEach(productComponent ->
                selectedComponents.add(productComponent.getComponent())
        );
        return selectedComponents;
    }

    public List<ProductComponent> getDifference(List<ProductComponent> inDatabase) {
        productComponents.forEach(
                toPersist -> inDatabase.removeIf(alreadySaved ->
                        toPersist.getId().equals(alreadySaved.getId())
                )
        );
        return inDatabase;
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
