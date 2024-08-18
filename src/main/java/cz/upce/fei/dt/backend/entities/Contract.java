package cz.upce.fei.dt.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
                @NamedAttributeNode(value = "contractProducts", subgraph = "contractProductSubgraph"),
                @NamedAttributeNode(value = "deadlines", subgraph = "deadlineUserSubgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "contractProductSubgraph",
                        attributeNodes = {@NamedAttributeNode("product")}
                ),
                @NamedSubgraph(
                        name = "deadlineUserSubgraph",
                        attributeNodes = {
                                @NamedAttributeNode("user"),
                                @NamedAttributeNode("status")
                        }
                )
        }
)
public class Contract {
    public static final int MAX_DESCRIPTION_LENGTH = 1500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = MAX_DESCRIPTION_LENGTH)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Double price = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ownPrice = false;

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

    @OneToMany(mappedBy = "contract", orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
    private Set<ContractProduct> contractProducts = new HashSet<>();

    @OneToMany(mappedBy = "contract")
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
    private Set<Deadline> deadlines = new HashSet<>();

    @OneToMany(mappedBy = "contract", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    @Builder.Default
    private Set<Note> notes = new HashSet<>();

    @OneToMany(mappedBy = "contract", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    @Builder.Default
    private Set<File> files = new HashSet<>();

    public List<Product> getSelectedProducts() {
        return contractProducts.stream().map(ContractProduct::getProduct).toList();
    }

    public Deadline getCurrentDeadline() {
        if (deadlines.isEmpty())
            return new Deadline();
        else{
            deadlines = deadlines.stream().sorted(Comparator.comparing(Deadline::getCreated).reversed()).collect(Collectors.toCollection(LinkedHashSet::new));
            return deadlines.iterator().next();
        }
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
