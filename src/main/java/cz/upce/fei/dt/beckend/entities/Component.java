package cz.upce.fei.dt.beckend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    private Integer amount = 0;

    @Column
    private Integer min;

    @Column
    @CreationTimestamp
    private LocalDateTime created;

    @Column
    @UpdateTimestamp
    private LocalDateTime updated;

    @OneToMany(mappedBy = "component", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<ProductComponent> productComponents = new ArrayList<>();

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
        Component component = (Component) o;
        return Objects.equals(id, component.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
