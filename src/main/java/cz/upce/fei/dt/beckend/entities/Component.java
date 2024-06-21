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
@Table(name = "components")
@NamedEntityGraph(
        name = "Component.eagerlyFetchProduct",
        attributeNodes = {
                @NamedAttributeNode("user"),
                @NamedAttributeNode(value = "productComponents", subgraph = "productComponentSubgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "productComponentSubgraph",
                        attributeNodes = {
                                @NamedAttributeNode("product")
                        }
                )
        }
)
public class Component {
    public static final int MAX_DESCRIPTION_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int inStock;

    @Column
    private Integer minInStock;

    @Column(nullable = false)
    private Double price;

    @Column
    @CreationTimestamp
    private LocalDateTime created;

    @Column
    @UpdateTimestamp
    private LocalDateTime updated;

    @OneToMany(mappedBy = "component", cascade = CascadeType.MERGE, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private Set<ProductComponent> productComponents = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public List<Product> getSelectedProduct() {
        List<Product> selectedProducts = new ArrayList<>();
        productComponents.forEach(productComponent ->
                selectedProducts.add(productComponent.getProduct())
        );
        return selectedProducts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return Objects.equals(id, component.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
