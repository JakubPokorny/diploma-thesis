package cz.upce.fei.dt.beckend.entities;

import cz.upce.fei.dt.beckend.entities.keys.ProductComponentKey;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

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
    private ProductComponentKey id = new ProductComponentKey();

    @Column(nullable = false)
    private int amount;

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
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ProductComponent that = (ProductComponent) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }
}
