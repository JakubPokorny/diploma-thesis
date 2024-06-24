package cz.upce.fei.dt.beckend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(name = "contacts")
public class Contact {
    public Contact() {
        invoiceAddress = new Address();
        deliveryAddress = new Address();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 8)
    private String ICO;

    @Column
    private String DIC;

    @Column(length = 100, nullable = false)
    private String client;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 20, nullable = false)
    private String phone;

    @Column
    @CreationTimestamp
    private LocalDateTime created;

    @Column
    @UpdateTimestamp
    private LocalDateTime updated;

    @OneToMany(mappedBy = "contact")
    @ToString.Exclude
    Set<Contract> contracts = new HashSet<>();

    @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_address_id", nullable = false)
    @ToString.Exclude
    private Address invoiceAddress;

    @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_address_id")
    @ToString.Exclude
    private Address deliveryAddress;

    public boolean hasDeliveryAddress() {
        return deliveryAddress != null && !deliveryAddress.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
