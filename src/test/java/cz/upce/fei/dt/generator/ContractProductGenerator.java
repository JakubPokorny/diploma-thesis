package cz.upce.fei.dt.generator;

import cz.upce.fei.dt.beckend.entities.Contract;
import cz.upce.fei.dt.beckend.entities.ContractProduct;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.keys.ContractProductKey;

public class ContractProductGenerator {
    public static ContractProduct generateContractProduct(Contract contract, Product product){
        return new ContractProduct(
                new ContractProductKey(contract.getId(), product.getId()),
                1,
                100.0,
                contract,
                product
        );
    }
}
