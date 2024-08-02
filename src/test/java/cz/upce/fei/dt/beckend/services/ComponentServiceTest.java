package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.data.provider.Query;
import cz.upce.fei.dt.beckend.dto.CheckStockDto;
import cz.upce.fei.dt.beckend.dto.ComponentMetrics;
import cz.upce.fei.dt.beckend.dto.IComponent;
import cz.upce.fei.dt.beckend.dto.IComponentCount;
import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.ProductComponent;
import cz.upce.fei.dt.beckend.repositories.ComponentRepository;
import cz.upce.fei.dt.beckend.services.filters.ComponentFilter;
import cz.upce.fei.dt.generator.ComponentGenerator;
import cz.upce.fei.dt.generator.ProductComponentGenerator;
import cz.upce.fei.dt.generator.ProductGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComponentServiceTest {
    @Mock
    private ComponentRepository componentRepository;
    @Mock
    private ProductComponentService productComponentService;
    @Mock
    private ProductService productService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private ComponentService componentService;

    @Mock
    private Query<Component, ComponentFilter> query;

    private static ComponentFilter componentFilter;
    private static List<Component> components;

    @BeforeAll
    static void beforeAll() {
        componentFilter = new ComponentFilter();
        components = List.of(
                new Component(),
                new Component(),
                new Component()
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchFromBackEnd() {
        when(query.getFilter()).thenReturn(Optional.of(componentFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(10);
        when(componentRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(components);

        Stream<Component> result = componentService.fetchFromBackEnd(query);

        List<Component> resultList = result.toList();
        assertEquals(components.size(), resultList.size());

        verify(componentRepository).findAll(any(Specification.class), any(Sort.class));
        verify(query, atLeastOnce()).getFilter();
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchFromBackEndWithPaging() {
        when(query.getFilter()).thenReturn(Optional.of(componentFilter));
        when(query.getOffset()).thenReturn(1);
        when(query.getLimit()).thenReturn(1);
        when(componentRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(components);

        Stream<Component> result = componentService.fetchFromBackEnd(query);

        List<Component> resultList = result.toList();
        assertEquals(1, resultList.size());

        verify(componentRepository).findAll(any(Specification.class), any(Sort.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void sizeInBackEnd() {
        when(query.getFilter()).thenReturn(Optional.of(componentFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(10);
        when(componentRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(components);

        int result = componentService.sizeInBackEnd(query);
        Assertions.assertEquals(3, result);

        verify(componentRepository).findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    void findAllByName() {
        IComponent iComponent1 = mock(IComponent.class);
        when(iComponent1.getId()).thenReturn(1L);
        when(iComponent1.getName()).thenReturn("Test 1");
        when(iComponent1.getPrice()).thenReturn(1.0);

        IComponent iComponent2 = mock(IComponent.class);
        when(iComponent2.getId()).thenReturn(2L);
        when(iComponent2.getName()).thenReturn("Test 2");
        when(iComponent2.getPrice()).thenReturn(2.0);

        List<IComponent> iComponents = List.of(iComponent1, iComponent2);
        Page<IComponent> page = new PageImpl<>(iComponents);

        when(componentRepository.findAllByName(any(Pageable.class), anyString())).thenReturn(page);

        Stream<Component> result = componentService.findAllByName(new Query<>("Test"));
        List<Component> resultList = result.toList();
        assertEquals(resultList.size(), iComponents.size());

        Component resultComponent1 = resultList.getFirst();
        assertEquals(iComponent1.getId(), resultComponent1.getId());
        assertEquals(iComponent1.getName(), resultComponent1.getName());
        assertEquals(iComponent1.getPrice(), resultComponent1.getPrice());

        Component resultComponent2 = resultList.get(1);
        assertEquals(iComponent2.getId(), resultComponent2.getId());
        assertEquals(iComponent2.getName(), resultComponent2.getName());
        assertEquals(iComponent2.getPrice(), resultComponent2.getPrice());

        verify(componentRepository).findAllByName(any(Pageable.class), anyString());
    }

    @Test
    void saveComponentFirstTime() {
        Product product1 = ProductGenerator.generateProduct(1L);
        Product product2 = ProductGenerator.generateProduct(2L);
        Product product3 = ProductGenerator.generateProduct(3L);

        Component component = ComponentGenerator.generateComponent(null);
        component.setProductComponents(Set.of(
                ProductComponentGenerator.generateProductComponent(component, product1),
                ProductComponentGenerator.generateProductComponent(component, product2),
                ProductComponentGenerator.generateProductComponent(component, product3)
        ));

        Component savedComponent = ComponentGenerator.generateComponent(1L);

        when(componentRepository.save(component)).thenReturn(savedComponent);
        when(productComponentService.findAllByComponentId(anyLong())).thenReturn(Collections.emptyList());
        when(componentRepository.save(savedComponent)).thenReturn(any());

        componentService.saveComponent(component);

        verify(componentRepository, times(1)).save(component);
        verify(componentRepository, times(1)).save(savedComponent);
        verify(productComponentService).findAllByComponentId(1L);
        verify(productComponentService).deleteAll(Collections.emptyList());
        verify(productService).updatePrices(Set.of(1L, 2L, 3L));
    }

    @Test
    void savePersistedComponent() {
        Product product1 = ProductGenerator.generateProduct(1L);
        Product product2 = ProductGenerator.generateProduct(2L);
        Product product3 = ProductGenerator.generateProduct(3L);

        Component component = ComponentGenerator.generateComponent(1L);

        ProductComponent pc1 = ProductComponentGenerator.generateProductComponent(component, product1);
        ProductComponent pc2 = ProductComponentGenerator.generateProductComponent(component, product2);
        ProductComponent pc3 = ProductComponentGenerator.generateProductComponent(component, product3);
        ArrayList<ProductComponent> productComponents = new ArrayList<>(Arrays.asList(pc1, pc2, pc3));

        component.setProductComponents(Set.of(pc1));

        when(productComponentService.findAllByComponentId(anyLong())).thenReturn(productComponents);
        when(componentRepository.save(component)).thenReturn(any());

        componentService.saveComponent(component);

        verify(productComponentService, times(1)).findAllByComponentId(1L);
        verify(productComponentService, times(1)).deleteAll(List.of(pc2, pc3));
        verify(componentRepository, times(1)).save(component);
        verify(productService, times(1)).updatePrices(Set.of(1L, 2L, 3L));
    }

    @Test
    void deletePersistedComponent() {
        Component component = ComponentGenerator.generateComponent(1L);
        List<Product> update = List.of(
                ProductGenerator.generateProduct(1L),
                ProductGenerator.generateProduct(2L)
        );

        when(productService.findAllByComponent(component)).thenReturn(update);

        componentService.deleteComponent(component);

        verify(componentRepository, times(1)).delete(component);
        verify(productService, times(1)).updatePrices(update);
    }

    @Test
    void deleteComponentWhereIdIsNull() {
        Component component = ComponentGenerator.generateComponent(null);
        List<Product> update = Collections.emptyList();

        when(productService.findAllByComponent(component)).thenReturn(update);

        componentService.deleteComponent(component);

        verify(componentRepository, times(1)).delete(component);
        verify(productService, times(1)).updatePrices(update);
    }

    @Test
    void updateAll() {
        CheckStockDto checkStockDto1 = CheckStockDto.builder()
                .componentID(1L)
                .componentsPerProduct(1)
                .componentsInStock(10)
                .minComponentsInStock(100)
                .email("email")
                .build();
        CheckStockDto checkStockDto2 = CheckStockDto.builder()
                .componentID(2L)
                .componentsPerProduct(1)
                .componentsInStock(0)
                .minComponentsInStock(100)
                .email(null)
                .build();

        List<CheckStockDto> componentToUpdate = List.of(checkStockDto1, checkStockDto2);
        List<CheckStockDto> underMinInStockLimit = List.of(checkStockDto1);

        componentService.updateAll(componentToUpdate);

        verify(componentRepository, times(1)).updateAmountById(1L, 10);
        verify(componentRepository, times(1)).updateAmountById(2L, 0);
        verify(emailService, times(1)).sendStockNotification(underMinInStockLimit);
    }

    @Test
    void updateAllUserByUser() {
        componentService.updateAllUserByUser(1L, 2L);
        verify(componentRepository).updateAllUserByUser(1L, 2L);
    }

    @Test
    void countMetrics() {
        IComponentCount iC1 = mock(IComponentCount.class); // supply
        IComponentCount iC2 = mock(IComponentCount.class); // supply
        IComponentCount iC3 = mock(IComponentCount.class); // missing
        IComponentCount iC4 = mock(IComponentCount.class); // inStock
        IComponentCount iC5 = mock(IComponentCount.class); // inStock
        IComponentCount iC6 = mock(IComponentCount.class); // supply

        when(iC1.getInStock()).thenReturn(0);
        when(iC1.getMinInStock()).thenReturn(0);
        when(iC2.getInStock()).thenReturn(10);
        when(iC2.getMinInStock()).thenReturn(15);
        when(iC3.getInStock()).thenReturn(-1);
        when(iC3.getMinInStock()).thenReturn(0);
        when(iC4.getInStock()).thenReturn(1);
        when(iC4.getMinInStock()).thenReturn(0);
        when(iC5.getInStock()).thenReturn(11);
        when(iC5.getMinInStock()).thenReturn(10);
        when(iC6.getInStock()).thenReturn(10);
        when(iC6.getMinInStock()).thenReturn(10);

        when(componentRepository.findAllComponentMetrics()).thenReturn(List.of(iC1, iC2, iC3, iC4, iC5, iC6));

        ComponentMetrics componentMetrics = componentService.countMetrics();
        assertEquals(6, componentMetrics.all());
        assertEquals(3, componentMetrics.supply());
        assertEquals(1, componentMetrics.missing());
        assertEquals(2, componentMetrics.inStock());

        verify(componentRepository, only()).findAllComponentMetrics();
    }
}