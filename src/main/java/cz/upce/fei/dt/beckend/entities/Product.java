package cz.upce.fei.dt.beckend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private List<ProductComponent> productComponents = new ArrayList<>();

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
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Product product = (Product) o;
        return getId() != null && Objects.equals(getId(), product.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}
