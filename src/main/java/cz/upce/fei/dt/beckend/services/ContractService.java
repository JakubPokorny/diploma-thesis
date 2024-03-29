package cz.upce.fei.dt.beckend.services;

import cz.upce.fei.dt.beckend.entities.Contract;
import cz.upce.fei.dt.beckend.entities.ContractProduct;
import cz.upce.fei.dt.beckend.repositories.ContractRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;
    private final DeadlineService deadlineService;
    private final ContractProductService contractProductService;
    private final FileService fileService;

    public Stream<Contract> findAll(int page, int pageSize) {
        return contractRepository.findAll(PageRequest.of(page, pageSize)).stream();
    }

    @Transactional
    public void saveContract(Contract contract) {
        contract.setUpdated(LocalDateTime.now());
        if (contract.getId() == null) { // new Contract
            Set<ContractProduct> contractProducts = contract.getContractProducts();

            contract.setContractProducts(null);
            Contract savedContract = contractRepository.save(contract);

            for (ContractProduct contractProduct : contractProducts) {
                contractProduct.setContract(savedContract);
                contractProductService.save(contractProduct);
            }

            contract.getCurrentDeadline().setContract(savedContract);
            deadlineService.save(contract.getCurrentDeadline());
        } else {
            contractRepository.save(contract);
            deadlineService.save(contract.getCurrentDeadline());
        }
    }

    @Transactional
    public void deleteContract(Contract contract) {
        contract.getFiles().forEach(fileService::delete);
        contractRepository.delete(contract);
    }
}
