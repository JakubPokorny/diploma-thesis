package cz.upce.fei.dt.backend.services;

import com.vaadin.flow.data.provider.Query;
import cz.upce.fei.dt.backend.dto.CheckStockDto;
import cz.upce.fei.dt.backend.dto.ICheckProduct;
import cz.upce.fei.dt.backend.dto.IProduct;
import cz.upce.fei.dt.backend.entities.Component;
import cz.upce.fei.dt.backend.entities.Product;
import cz.upce.fei.dt.backend.entities.ProductComponent;
import cz.upce.fei.dt.backend.repositories.ProductRepository;
import cz.upce.fei.dt.backend.services.filters.DashboardFilter;
import cz.upce.fei.dt.backend.services.filters.ProductFilter;
import cz.upce.fei.dt.generator.ComponentGenerator;
import cz.upce.fei.dt.generator.ProductComponentGenerator;
import cz.upce.fei.dt.generator.ProductGenerator;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductComponentService productComponentService;

    @InjectMocks
    private ProductService productService;

    @Mock
    private Query<Product, ProductFilter> query;

    private static ProductFilter productFilter;
    private static List<Product> products;

    @BeforeAll
    static void beforeAll() {
        productFilter = new ProductFilter();
        products = List.of(
                new Product(),
                new Product(),
                new Product()
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchFromBackEnd() {
        when(query.getFilter()).thenReturn(Optional.of(productFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(10);
        when(productRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(products);

        Stream<Product> result = productService.fetchFromBackEnd(query);

        List<Product> resultList = result.toList();
        assertEquals(products.size(), resultList.size());

        verify(productRepository).findAll(any(Specification.class), any(Sort.class));
        verify(query, atLeastOnce()).getFilter();
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchFromBackEndWithPaging() {
        when(query.getFilter()).thenReturn(Optional.of(productFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(1);
        when(productRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(products);

        Stream<Product> result = productService.fetchFromBackEnd(query);

        List<Product> resultList = result.toList();
        assertEquals(1, resultList.size());

        verify(productRepository).findAll(any(Specification.class), any(Sort.class));
        verify(query, atLeastOnce()).getFilter();
    }

    @SuppressWarnings("unchecked")
    @Test
    void sizeInBackEnd() {
        when(query.getFilter()).thenReturn(Optional.of(productFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(10);
        when(productRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(products);

        int result = productService.sizeInBackEnd(query);

        assertEquals(products.size(), result);

        verify(productRepository).findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    void findAllByName() {
        IProduct iProduct1 = mock(IProduct.class);
        when(iProduct1.getId()).thenReturn(1L);
        when(iProduct1.getName()).thenReturn("Test 1");
        when(iProduct1.getSellingPrice()).thenReturn(100.0);

        IProduct iProduct2 = mock(IProduct.class);
        when(iProduct2.getId()).thenReturn(1L);
        when(iProduct2.getName()).thenReturn("Test 2");
        when(iProduct2.getSellingPrice()).thenReturn(200.0);

        List<IProduct> iProducts = List.of(iProduct1, iProduct2);
        Page<IProduct> page = new PageImpl<>(iProducts);

        when(productRepository.findAllByName(any(Pageable.class), anyString())).thenReturn(page);

        Stream<Product> result = productService.findAllByName(new Query<>("Test"));
        List<Product> resultList = result.toList();
        assertEquals(resultList.size(), iProducts.size());

        Product resultProduct1 = resultList.getFirst();
        assertEquals(iProduct1.getId(), resultProduct1.getId());
        assertEquals(iProduct1.getName(), resultProduct1.getName());
        assertEquals(iProduct1.getSellingPrice(), resultProduct1.getSellingPrice());

        Product resultProduct2 = resultList.get(1);
        assertEquals(iProduct2.getId(), resultProduct2.getId());
        assertEquals(iProduct2.getName(), resultProduct2.getName());
        assertEquals(iProduct2.getSellingPrice(), resultProduct2.getSellingPrice());

        verify(productRepository).findAllByName(any(Pageable.class), anyString());
    }

    @Test
    void findAllByID() {
        List<Long> productIDs = List.of(1L, 2L, 3L);

        List<ICheckProduct> iCheckProducts = List.of(
                mock(ICheckProduct.class),
                mock(ICheckProduct.class),
                mock(ICheckProduct.class)
        );

        when(productRepository.findAllByID(productIDs)).thenReturn(iCheckProducts);

        List<CheckStockDto> result = productService.findAllByID(productIDs);
        assertEquals(result.size(), iCheckProducts.size());

        verify(productRepository).findAllByID(productIDs);
    }

    @Test
    void findAllByComponentReturnEmptyList() {
        List<Product> result = productService.findAllByComponent(new Component());
        assertEquals(Collections.emptyList(), result);

        verify(productRepository, times(0)).findAllByComponentId(anyLong());
    }

    @Test
    void findAllByComponent() {
        when(productRepository.findAllByComponentId(anyLong())).thenReturn(products);

        List<Product> result = productService.findAllByComponent(mock(Component.class));

        assertEquals(result.size(), products.size());

        verify(productRepository).findAllByComponentId(anyLong());
    }

    @Test
    void saveProductFirstTime() {
        Component component1 = ComponentGenerator.generateComponent(1L);
        Component component2 = ComponentGenerator.generateComponent(2L);
        Component component3 = ComponentGenerator.generateComponent(3L);

        Product product = ProductGenerator.generateProduct(null);
        product.setProductComponents(Set.of(
                ProductComponentGenerator.generateProductComponent(component1, product),
                ProductComponentGenerator.generateProductComponent(component2, product),
                ProductComponentGenerator.generateProductComponent(component3, product)
        ));

        Product savedProduct = ProductGenerator.generateProduct(1L);

        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productComponentService.findAllByProductId(savedProduct.getId())).thenReturn(Collections.emptyList());
        when(productRepository.save(savedProduct)).thenReturn(any());

        productService.saveProduct(product);

        verify(productRepository, times(1)).save(product);
        verify(productRepository, times(1)).save(savedProduct);
        verify(productComponentService).findAllByProductId(savedProduct.getId());
        verify(productComponentService).deleteAll(Collections.emptyList());
    }

    @Test
    void savePersistedProduct() {
        Component component1 = ComponentGenerator.generateComponent(1L);
        Component component2 = ComponentGenerator.generateComponent(2L);
        Component component3 = ComponentGenerator.generateComponent(3L);

        Product product = ProductGenerator.generateProduct(1L);

        ProductComponent pc1 = ProductComponentGenerator.generateProductComponent(component1, product);
        ProductComponent pc2 = ProductComponentGenerator.generateProductComponent(component2, product);
        ProductComponent pc3 = ProductComponentGenerator.generateProductComponent(component3, product);
        ArrayList<ProductComponent> productComponents = new ArrayList<>(Arrays.asList(pc1, pc2, pc3));

        product.setProductComponents(Set.of(pc1));

        when(productComponentService.findAllByProductId(product.getId())).thenReturn(productComponents);
        when(productRepository.save(product)).thenReturn(any());

        productService.saveProduct(product);

        verify(productComponentService).findAllByProductId(product.getId());
        verify(productComponentService).deleteAll(List.of(pc2, pc3));
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void deletePersistedProduct() {
        Product product = ProductGenerator.generateProduct(1L);

        productService.deleteProduct(product);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProductWhereIdIsNull() {
        Product product = ProductGenerator.generateProduct(null);

        productService.deleteProduct(product);

        verify(productRepository).delete(product);
    }

    @Test
    void updatePrices() {
        Component component1 = mock(Component.class);
        when(component1.getPrice()).thenReturn(100.0);
        Component component2 = mock(Component.class);
        when(component2.getPrice()).thenReturn(200.0);
        Component component3 = mock(Component.class);
        when(component3.getPrice()).thenReturn(300.0);

        ProductComponent pc1 = mock(ProductComponent.class);
        when(pc1.getComponentsPerProduct()).thenReturn(10);
        when(pc1.getComponent()).thenReturn(component1);
        ProductComponent pc2 = mock(ProductComponent.class);
        when(pc2.getComponentsPerProduct()).thenReturn(20);
        when(pc2.getComponent()).thenReturn(component2);
        ProductComponent pc3 = mock(ProductComponent.class);
        when(pc3.getComponentsPerProduct()).thenReturn(30);
        when(pc3.getComponent()).thenReturn(component3);

        Product product1 = ProductGenerator.generateProduct(1L);
        product1.setProductComponents(Set.of(pc1, pc2, pc3));
        Product product2 = ProductGenerator.generateProduct(2L);
        product2.setProductComponents(Set.of(pc2, pc3));
        Product product3 = ProductGenerator.generateProduct(3L);
        product3.setProductComponents(Set.of(pc3));

        List<Product> input = List.of(product1, product2, product3);
        product1.setProductionPrice(10 * 1000 + 20 * 200 + 30 * 300.0);
        product1.setSellingPrice((10 * 1000 + 20 * 200 + 30 * 300.0) * 1.1);
        product2.setProductionPrice(20 * 200 + 30 * 300.0);
        product2.setSellingPrice((20 * 200 + 30 * 300.0) * 1.1);
        product3.setProductionPrice(30 * 300.0);
        product3.setSellingPrice((30 * 300) * 1.1);
        List<Product> toSave = List.of(product1, product2, product3);

        when(productRepository.findAllById(anyList())).thenReturn(input);

        productService.updatePrices(List.of(1L, 2L, 3L));

        verify(productRepository).saveAll(toSave);
    }

    @Test
    void getMostSaleableProducts() {
        DashboardFilter dashboardFilter = mock(DashboardFilter.class);

        productService.getMostSaleableProducts(dashboardFilter);

        verify(productRepository).getMostSaleableProducts(anySet(), anySet(), any(), any());
    }
}