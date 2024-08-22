package cz.upce.fei.dt.generator;

import cz.upce.fei.dt.backend.entities.Contract;
import cz.upce.fei.dt.backend.entities.ContractProduct;
import cz.upce.fei.dt.backend.entities.Product;
import cz.upce.fei.dt.backend.entities.keys.ContractProductKey;

public class ContractProductGenerator {
    public static ContractProduct generateContractProduct(Contract contract, Product product){
        return new ContractProduct(
                new ContractProductKey(contract.getId(), product.getId()),
                1,
                50.0,
                100.0,
                contract,
                product
        );
    }
}
