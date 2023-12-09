package cz.upce.fei.dt.beckend.entities;

import cz.upce.fei.dt.beckend.entities.keys.ProductComponentKey;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_components")
public class ProductComponent {

    @EmbeddedId
    private ProductComponentKey id;

    @Column(nullable = false)
    private int amount;

    @ManyToOne
    @JoinColumn(name = "component_id", nullable = false)
    private Component component;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
