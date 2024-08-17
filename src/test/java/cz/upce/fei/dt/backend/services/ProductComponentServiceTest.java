package cz.upce.fei.dt.backend.services;

import cz.upce.fei.dt.backend.entities.ProductComponent;
import cz.upce.fei.dt.backend.repositories.ProductComponentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductComponentServiceTest {
    @Mock
    private ProductComponentRepository productComponentRepository;

    @InjectMocks
    private ProductComponentService productComponentService;

    @Test
    void findAllByComponentId() {
        productComponentService.findAllByComponentId(1L);
        verify(productComponentRepository).findAllByComponentId(1L);
    }

    @Test
    void findAllByProductId() {
        productComponentService.findAllByProductId(1L);
        verify(productComponentRepository).findAllByProductId(1L);
    }

    @Test
    void deleteAll() {
        List<ProductComponent> productComponents = List.of(new ProductComponent());
        productComponentService.deleteAll(productComponents);
        verify(productComponentRepository).deleteAll(productComponents);
    }
}