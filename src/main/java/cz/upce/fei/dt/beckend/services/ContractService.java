package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.fei.dt.beckend.entities.Contract;
import cz.upce.fei.dt.beckend.entities.ContractProduct;
import cz.upce.fei.dt.beckend.entities.Deadline;
import cz.upce.fei.dt.beckend.repositories.ContractRepository;
import cz.upce.fei.dt.beckend.services.filters.ContractFilter;
import cz.upce.fei.dt.beckend.services.specifications.ContractSpec;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ContractService extends AbstractBackEndDataProvider<Contract, ContractFilter> {
    private final ContractRepository contractRepository;
    private final DeadlineService deadlineService;
    private final ContractProductService contractProductService;
    private final FileService fileService;

    @Override
    public Stream<Contract> fetchFromBackEnd(Query<Contract, ContractFilter> query) {
        Specification<Contract> spec = ContractSpec.filterBy(query.getFilter().orElse(new ContractFilter()));
        Stream<Contract> stream = contractRepository.findAll(spec, VaadinSpringDataHelpers.toSpringDataSort(query)).stream();

        if (query.getFilter().isPresent()) {
            stream = stream.filter(contract -> query.getFilter().get().filter(contract));
        }

        return stream.skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public int sizeInBackEnd(Query<Contract, ContractFilter> query) {
        return (int) fetchFromBackEnd(query).count();
    }

    @Transactional
    public void saveContract(Contract contract) {
        contract.setUpdated(LocalDateTime.now());
        Deadline deadline = contract.getCurrentDeadline();
        if (contract.getId() == null) { // new Contract
            Set<ContractProduct> contractProducts = contract.getContractProducts();

            contract.setContractProducts(null);
            contract = contractRepository.save(contract);

            for (ContractProduct contractProduct : contractProducts) {
                contractProduct.setContract(contract);
            }
            contract.setContractProducts(contractProducts);
            contractProductService.saveAll(contract);

            deadline.setContract(contract);
        } else {
            contractProductService.saveAll(contract);
            contractRepository.save(contract);
        }
        deadlineService.save(deadline);
    }

    @Transactional
    public void deleteContract(Contract contract) {
        fileService.deleteAll(contract.getId());
        contractProductService.deleteAll(contract);
        deadlineService.deleteAll(contract.getId());
        contractRepository.delete(contract);
    }

    public int getCountAll() {
        return contractRepository.countAll();
    }

}
