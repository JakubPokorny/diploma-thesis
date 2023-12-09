package cz.upce.fei.dt.beckend.entities.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ProductComponentKey implements Serializable {
    @Column(name = "product_id", insertable = false, updatable = false)
    private Long productId;
    @Column(name = "component_id", insertable = false, updatable = false)
    private Long componentId;
}
