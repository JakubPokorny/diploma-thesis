package cz.upce.fei.dt.backend.services;

import com.vaadin.flow.data.provider.Query;
import cz.upce.fei.dt.backend.entities.Contract;
import cz.upce.fei.dt.backend.entities.ContractProduct;
import cz.upce.fei.dt.backend.entities.Deadline;
import cz.upce.fei.dt.backend.entities.keys.ContractProductKey;
import cz.upce.fei.dt.backend.repositories.ContractRepository;
import cz.upce.fei.dt.backend.services.filters.ContractFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {
    @Mock
    private ContractRepository contractRepository;
    @Mock
    private DeadlineService deadlineService;
    @Mock
    private ContractProductService contractProductService;
    @Mock
    private FileService fileService;

    @InjectMocks
    private ContractService contractService;

    @Mock
    private Query<Contract, ContractFilter> query;
    @Mock
    private ContractFilter contractFilter;

    private static List<Contract> contracts;

    @BeforeAll
    static void beforeAll() {
        contracts = List.of(
                new Contract(),
                new Contract(),
                new Contract()
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchFromBackEnd() {
        when(query.getFilter()).thenReturn(Optional.of(contractFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(10);
        when(contractFilter.filter(any(Contract.class))).thenReturn(true);
        when(contractRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(contracts);

        Stream<Contract> result = contractService.fetchFromBackEnd(query);

        List<Contract> resultList = result.toList();
        assertEquals(contracts.size(), resultList.size());

        verify(contractRepository).findAll(any(Specification.class), any(Sort.class));
        verify(query, atLeastOnce()).getFilter();
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchFromBackEndWithPaging() {
        when(query.getFilter()).thenReturn(Optional.of(contractFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(1);
        when(contractFilter.filter(any(Contract.class))).thenReturn(true);
        when(contractRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(contracts);

        Stream<Contract> result = contractService.fetchFromBackEnd(query);

        List<Contract> resultList = result.toList();
        assertEquals(1, resultList.size());

        verify(contractRepository).findAll(any(Specification.class), any(Sort.class));
        verify(query, atLeastOnce()).getFilter();
    }

    @SuppressWarnings("unchecked")
    @Test
    void sizeInBackEnd() {
        when(query.getFilter()).thenReturn(Optional.of(contractFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(10);
        when(contractFilter.filter(any(Contract.class))).thenReturn(true);
        when(contractRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(contracts);

        int result = contractService.sizeInBackEnd(query);

        assertEquals(contracts.size(), result);

        verify(contractRepository).findAll(any(Specification.class), any(Sort.class));
        verify(query, atLeastOnce()).getFilter();
    }

    @Test
    void saveNewContract() {
        Set<ContractProduct> contractProducts = Set.of(
                ContractProduct.builder().id(new ContractProductKey(null, 1L)).build(),
                ContractProduct.builder().id(new ContractProductKey(null, 2L)).build(),
                ContractProduct.builder().id(new ContractProductKey(null, 3L)).build()
        );

        Contract contract = new Contract();
        contract.setDeadlines(Set.of(new Deadline()));
        contract.setContractProducts(contractProducts);

        Contract savedContract = new Contract();
        savedContract.setId(1L);

        when(contractRepository.save(any(Contract.class))).thenReturn(savedContract);

        contractService.saveContract(contract);

        ArgumentCaptor<Contract> contractCaptor = ArgumentCaptor.forClass(Contract.class);
        verify(contractRepository, only()).save(contractCaptor.capture());
        assertNull(contractCaptor.getValue().getContractProducts());

        verify(contractProductService, only()).saveAll(contractCaptor.capture());
        assertEquals(contractProducts.size(), contractCaptor.getValue().getContractProducts().size());
        for (ContractProduct cp : contractCaptor.getValue().getContractProducts()) {
            assertEquals(1L, cp.getContract().getId());
        }

        ArgumentCaptor<Deadline> deadlineCaptor = ArgumentCaptor.forClass(Deadline.class);
        verify(deadlineService).save(deadlineCaptor.capture());
        assertEquals(contractCaptor.getValue(), deadlineCaptor.getValue().getContract());
    }

    @Test
    void saveContract() {
        Contract contract = mock(Contract.class);
        Deadline deadline = mock(Deadline.class);

        when(contract.getId()).thenReturn(1L);
        when(contract.getCurrentDeadline()).thenReturn(deadline);

        contractService.saveContract(contract);

        verify(contractProductService, only()).saveAll(contract);
        verify(contractRepository, only()).save(contract);
        verify(deadlineService).save(deadline);
    }

    @Test
    void deleteContract() {
        Contract contract = mock(Contract.class);
        when(contract.getId()).thenReturn(1L);

        contractService.deleteContract(contract);

        verify(fileService).deleteAll(anyLong());
        verify(contractProductService).deleteAll(eq(contract));
        verify(deadlineService).deleteAll(anyLong());
        verify(contractRepository).delete(eq(contract));
    }

    @Test
    void getCountAll() {
        contractService.getCountAll();
        verify(contractRepository).countAll();
    }
}