package cz.upce.fei.dt.backend.entities;

import cz.upce.fei.dt.backend.entities.keys.ProductComponentKey;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_components")
public class ProductComponent {

    @EmbeddedId
    @Builder.Default
    private ProductComponentKey id = new ProductComponentKey();

    @Column(nullable = false)
    private int componentsPerProduct;

    @ManyToOne
    @MapsId("componentId")
    @JoinColumn(name = "component_id")
    private Component component;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    public void setComponent(Component component) {
        this.component = component;
        id.setComponentId(component.getId());
    }

    public void setProduct(Product product) {
        this.product = product;
        id.setProductId(product.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductComponent that = (ProductComponent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
