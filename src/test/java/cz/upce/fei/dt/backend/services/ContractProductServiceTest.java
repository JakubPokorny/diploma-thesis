package cz.upce.fei.dt.backend.services;

import cz.upce.fei.dt.backend.dto.CheckStockDto;
import cz.upce.fei.dt.backend.entities.Contract;
import cz.upce.fei.dt.backend.entities.ContractProduct;
import cz.upce.fei.dt.backend.repositories.ContractProductRepository;
import cz.upce.fei.dt.generator.CheckStockDtoGenerator;
import cz.upce.fei.dt.generator.ContractProductGenerator;
import cz.upce.fei.dt.generator.ProductGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractProductServiceTest {
    @Mock
    private ContractProductRepository contractProductRepository;
    @Mock
    private ComponentService componentService;
    @Mock
    private ProductService productService;

    @InjectMocks
    private ContractProductService contractProductService;

    @Captor
    private ArgumentCaptor<Set<ContractProduct>> contractProductCaptor;
    @Captor
    private ArgumentCaptor<Collection<CheckStockDto>> checkStockDtoCaptor;

    private static Contract contract;
    private static ContractProduct cp1;
    private static ContractProduct cp2;
    private static ContractProduct cp3;

    @BeforeAll
    static void beforeAll() {
        contract = Contract.builder()
                .id(1L)
                .build();
        cp1 = ContractProductGenerator.generateContractProduct(contract, ProductGenerator.generateProduct(1L));
        cp2 = ContractProductGenerator.generateContractProduct(contract, ProductGenerator.generateProduct(2L));
        cp3 = ContractProductGenerator.generateContractProduct(contract, ProductGenerator.generateProduct(3L));
    }

    @Test
    void save() {
        contractProductService.save(new ContractProduct());
        verify(contractProductRepository).save(any(ContractProduct.class));
    }

    @Test
    void saveAllNewOnes() {
        contract.setContractProducts(Set.of(cp1, cp2, cp3));
        List<CheckStockDto> componentToUpdate = CheckStockDtoGenerator.generateCheckStockDto(List.of(1L, 2L, 3L));

        when(contractProductRepository.findAllByContractId(anyLong())).thenReturn(Collections.emptySet());
        when(productService.findAllByID(argThat(list -> list != null && ((List<?>) list).size() == 3))).thenReturn(componentToUpdate);
        when(productService.findAllByID(Collections.emptyList())).thenReturn(Collections.emptyList());

        contractProductService.saveAll(contract);

        verify(contractProductRepository).findAllByContractId(contract.getId());

        verify(productService).findAllByID(argThat(list -> list != null && ((List<?>) list).size() == 3));
        verify(productService).findAllByID(Collections.emptyList());

        verify(contractProductRepository).saveAll(contractProductCaptor.capture());
        assertEquals(3, contractProductCaptor.getValue().size());

        verify(componentService).updateAll(checkStockDtoCaptor.capture());
        assertEquals(3, checkStockDtoCaptor.getValue().size());
        for (CheckStockDto checkStockDto : checkStockDtoCaptor.getValue()) {
            assertEquals(100 - 3, checkStockDto.getComponentsInStock());
        }
    }

    @Test
    void AddComponentForExistingProduct() {
        contract.setContractProducts(Set.of(cp1, cp2, cp3));
        List<CheckStockDto> componentToUpdate = CheckStockDtoGenerator.generateCheckStockDto(List.of(1L, 2L, 3L));

        when(contractProductRepository.findAllByContractId(anyLong())).thenReturn(Set.of(cp1, cp2));
        when(productService.findAllByID(argThat(list -> list != null && ((List<?>) list).size() == 3))).thenReturn(componentToUpdate);
        when(productService.findAllByID(Collections.emptyList())).thenReturn(Collections.emptyList());

        contractProductService.saveAll(contract);

        verify(contractProductRepository).findAllByContractId(contract.getId());

        verify(productService).findAllByID(argThat(list -> list != null && ((List<?>) list).size() == 3));
        verify(productService).findAllByID(Collections.emptyList());

        verify(contractProductRepository).saveAll(contractProductCaptor.capture());
        assertEquals(3, contractProductCaptor.getValue().size());

        verify(componentService).updateAll(checkStockDtoCaptor.capture());
        assertEquals(3, checkStockDtoCaptor.getValue().size());
        for (CheckStockDto checkStockDto : checkStockDtoCaptor.getValue()) {
            assertEquals(100 - 1, checkStockDto.getComponentsInStock());
        }
    }

    @Test
    void RemoveComponentForExistingProduct() {
        contract.setContractProducts(Set.of(cp1));
        List<CheckStockDto> componentToUpdate = CheckStockDtoGenerator.generateCheckStockDto(List.of(1L));
        List<CheckStockDto> orphans = CheckStockDtoGenerator.generateCheckStockDto(List.of(2L, 3L));

        when(contractProductRepository.findAllByContractId(anyLong())).thenReturn(Set.of(cp1, cp2, cp3));
        when(productService.findAllByID(argThat(list -> list != null && ((List<?>) list).size() == 1))).thenReturn(componentToUpdate);
        when(productService.findAllByID(argThat(list -> list != null && ((List<?>) list).size() == 2))).thenReturn(orphans);

        contractProductService.saveAll(contract);

        verify(contractProductRepository).findAllByContractId(contract.getId());

        verify(productService).findAllByID(argThat(list -> list != null && ((List<?>) list).size() == 1));
        verify(productService).findAllByID(argThat(list -> list != null && ((List<?>) list).size() == 2));

        verify(contractProductRepository).saveAll(contractProductCaptor.capture());
        assertEquals(1, contractProductCaptor.getValue().size());

        verify(componentService).updateAll(checkStockDtoCaptor.capture());
        assertEquals(3, checkStockDtoCaptor.getValue().size());
        for (CheckStockDto checkStockDto : checkStockDtoCaptor.getValue()) {
            assertEquals(100 + 2, checkStockDto.getComponentsInStock());
        }
    }

    @Test
    void AddAndRemoveComponentForExistingProduct() {
        contract.setContractProducts(Set.of(cp1, cp2));
        List<CheckStockDto> componentToUpdate = CheckStockDtoGenerator.generateCheckStockDto(List.of(1L, 2L));
        List<CheckStockDto> orphans = CheckStockDtoGenerator.generateCheckStockDto(List.of(3L));

        when(contractProductRepository.findAllByContractId(anyLong())).thenReturn(Set.of(cp2, cp3));
        when(productService.findAllByID(argThat(list -> list != null && ((List<?>) list).size() == 2))).thenReturn(componentToUpdate);
        when(productService.findAllByID(argThat(list -> list != null && ((List<?>) list).size() == 1))).thenReturn(orphans);

        contractProductService.saveAll(contract);

        verify(contractProductRepository).findAllByContractId(contract.getId());

        verify(productService).findAllByID(argThat(list -> list != null && ((List<?>) list).size() == 2));
        verify(productService).findAllByID(argThat(list -> list != null && ((List<?>) list).size() == 1));

        verify(contractProductRepository).saveAll(contractProductCaptor.capture());
        assertEquals(2, contractProductCaptor.getValue().size());

        verify(componentService).updateAll(checkStockDtoCaptor.capture());
        assertEquals(3, checkStockDtoCaptor.getValue().size());
        for (CheckStockDto checkStockDto : checkStockDtoCaptor.getValue()) {
            assertEquals(100, checkStockDto.getComponentsInStock());
        }
    }

    @Test
    void deleteAll() {
        contract.setContractProducts(Set.of(cp1, cp2, cp3));
        List<CheckStockDto> componentToUpdate = CheckStockDtoGenerator.generateCheckStockDto(List.of(1L, 2L, 3L));

        when(productService.findAllByID(argThat(list -> list != null && ((List<?>) list).size() == 3))).thenReturn(componentToUpdate);

        contractProductService.deleteAll(contract);

        verify(productService).findAllByID(argThat(list -> list != null && ((List<?>) list).size() == 3));

        verify(componentService).updateAll(checkStockDtoCaptor.capture());
        assertEquals(3, checkStockDtoCaptor.getValue().size());
        for (CheckStockDto checkStockDto : checkStockDtoCaptor.getValue()) {
            assertEquals(100 + 3, checkStockDto.getComponentsInStock());
        }

        verify(contractProductRepository).deleteAll(contractProductCaptor.capture());
        assertEquals(3, contractProductCaptor.getValue().size());
    }

}