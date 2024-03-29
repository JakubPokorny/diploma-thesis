package cz.upce.fei.dt.beckend.dto;

import cz.upce.fei.dt.beckend.entities.keys.ContractProductKey;

public interface IContractProduct {
    ContractProductKey getId();
    int getAmount();
}
