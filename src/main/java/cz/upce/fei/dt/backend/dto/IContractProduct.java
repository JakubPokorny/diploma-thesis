package cz.upce.fei.dt.backend.dto;

import cz.upce.fei.dt.backend.entities.keys.ContractProductKey;

public interface IContractProduct {
    ContractProductKey getId();
    int getAmount();
}
