package cz.upce.fei.dt.beckend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "contracts")
@NamedEntityGraph(
        name = "Contract.eagerlyFetchProductsAndContactAndUser",
        attributeNodes = {
                @NamedAttributeNode(value = "contact"),
                @NamedAttributeNode(value = "files"),
                @NamedAttributeNode(value = "notes"),
                @NamedAttributeNode(value = "contractProducts", subgraph = "contractProductSubgraph"),
                @NamedAttributeNode(value = "deadlines", subgraph = "deadlinesSubgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "contractProductSubgraph",
                        attributeNodes = {@NamedAttributeNode("product")}
                ),
                @NamedSubgraph(
                        name = "deadlinesSubgraph",
                        attributeNodes = {@NamedAttributeNode("user")}
                )
        }
)
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @CreationTimestamp
    private LocalDateTime created;

    @Column
    @UpdateTimestamp
    private LocalDateTime updated;

    @ManyToOne
    @JoinColumn(name = "contact_id", nullable = false)
    @ToString.Exclude
    private Contact contact;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private Set<ContractProduct> contractProducts = new HashSet<>();

    @OneToMany(mappedBy = "contract", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    @JsonIgnore
    private Set<Deadline> deadlines = new HashSet<>();

    @OneToMany(mappedBy = "contract", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private Set<Note> notes = new HashSet<>();

    @OneToMany(mappedBy = "contract", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private Set<File> files = new HashSet<>();


    public List<Product> getSelectedProducts() {
        return contractProducts.stream().map(ContractProduct::getProduct).toList();
    }

    public Deadline getCurrentDeadline() {
        if (deadlines.isEmpty())
            return new Deadline();
        else
            return deadlines.iterator().next();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return Objects.equals(id, contract.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
