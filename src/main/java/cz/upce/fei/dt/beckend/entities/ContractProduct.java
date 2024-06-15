package cz.upce.fei.dt.beckend.entities;

import cz.upce.fei.dt.beckend.entities.keys.ContractProductKey;
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
@Table(name = "contract_products")
public class ContractProduct {
    @EmbeddedId
    private ContractProductKey id = new ContractProductKey();

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private Double pricePerPiece;

    @ManyToOne
    @MapsId("contractId")
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    public void setContract(Contract contract) {
        this.contract = contract;
        id.setContractId(contract.getId());
    }

    public void setProduct(Product product) {
        this.product = product;
        id.setProductId(product.getId());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContractProduct that = (ContractProduct) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
