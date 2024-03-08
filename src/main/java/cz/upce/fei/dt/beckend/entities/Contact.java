package cz.upce.fei.dt.beckend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
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
    private long id;

    @Column(precision = 8, scale = 0)
    private String ICO;

    @Column
    private String DIC;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 20, nullable = false)
    private  String phone;

    @Column
    @UpdateTimestamp
    private LocalDateTime updated;

    @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_address_id", nullable = false)
    private Address invoiceAddress;

    @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_address_id")   
    private Address deliveryAddress;

    public boolean hasDeliveryAddress() {
        return deliveryAddress != null && !deliveryAddress.isEmpty();
    }

}
