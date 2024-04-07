package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.fei.dt.beckend.dto.CheckStockDto;
import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.ProductComponent;
import cz.upce.fei.dt.beckend.repositories.ComponentRepository;
import cz.upce.fei.dt.beckend.repositories.ProductComponentRepository;
import cz.upce.fei.dt.beckend.services.filters.ComponentFilter;
import cz.upce.fei.dt.beckend.services.specifications.ComponentSpec;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ComponentService {
    private final ComponentRepository componentRepository;
    private final ProductComponentRepository productComponentRepository;
    private final EmailService emailService;

    public Stream<Component> findAllComponentsIdAndName(int page, int pageSize, String searchTerm) {
        return componentRepository.findAllComponentsIDAndName(PageRequest.of(page, pageSize), searchTerm)
                .stream()
                .map(iComponent -> Component.builder()
                        .id(iComponent.getId())
                        .name(iComponent.getName())
                        .build()
                );
    }

    @Transactional
    public void saveComponent(Component component) {
        component.setUpdated(LocalDateTime.now());
        if (component.getId() == null) {
            Set<ProductComponent> productComponents = component.getProductComponents();
            component.setProductComponents(null);
            Component savedComponent = componentRepository.save(component);
            for (ProductComponent productComponent : productComponents) {
                productComponent.setComponent(savedComponent);
                productComponentRepository.save(productComponent);
            }
        } else {
            productComponentRepository.deleteAll(
                    component.getDifference(
                            productComponentRepository.findAllByComponentId(component.getId())
                    )
            );
            componentRepository.save(component);
        }
    }

    public void deleteComponent(Component component) throws Exception {
        Component wantToDelete = componentRepository.findById(component.getId())
                .orElseThrow(() -> new Exception("Komponenta " + component.getName() + " nenalezana."));
        componentRepository.delete(wantToDelete);
    }

    @Transactional
    public void updateAllInStockAssigned(Collection<CheckStockDto> assigned) {
        assigned.forEach(stockDto -> {
            componentRepository.updateAmountById(stockDto.getComponentId(), stockDto.getComponentsInStock());
            if (stockDto.getMinComponentsInStock() != null && stockDto.getComponentsInStock() < stockDto.getMinComponentsInStock() && stockDto.getEmail() != null) {
                emailService.sendStockNotification(
                        stockDto.getEmail(),
                        stockDto.getComponentName(),
                        stockDto.getComponentsInStock(),
                        stockDto.getMinComponentsInStock()
                );
            }
        });
    }

    public Stream<Component> findAll(Query<Component, ComponentFilter> query) {
        Specification<Component> spec = ComponentSpec.filterBy(query.getFilter().orElse(new ComponentFilter()));
        return componentRepository.findAll(spec, VaadinSpringDataHelpers.toSpringPageRequest(query))
                .stream();
    }

    public int getCount(Query<Component, ComponentFilter> query) {
        Specification<Component> spec = ComponentSpec.filterBy(query.getFilter().orElse(new ComponentFilter()));
        return (int) componentRepository.findAll(spec, VaadinSpringDataHelpers.toSpringPageRequest(query)).stream().count();
    }

    //todo error because I reduce rows -> components size 22 but grid expect 41 via getCount.
//    public Stream<Component> findAll(Query<Component, ComponentFilter> query) {
//        HashMap<Long, Component> components = new HashMap<>();
//        Specification<Component> spec = ComponentSpec.filterBy(query.getFilter().orElse(new ComponentFilter()));
//        componentRepository.findAllIComponent(spec, VaadinSpringDataHelpers.toSpringPageRequest(query))
//                .stream()
//                .forEach(iComponent -> {
//                    if (components.containsKey(iComponent.getId())) {
//                        components.get(iComponent.getId()).getProductComponents().add(toProductComponent(iComponent));
//                    } else {
//                        components.put(iComponent.getId(), toComponent(iComponent));
//                    }
//                });
//        return components.values().stream();
//    }
//
//
//
//    public int getCount(Query<Component, ComponentFilter> query) {
//        Specification<Component> spec = ComponentSpec.filterBy(query.getFilter().orElse(new ComponentFilter()));
//        return (int) componentRepository.findAllIComponent(spec, VaadinSpringDataHelpers.toSpringPageRequest(query))
//                .stream()
//                .count();
//    }
//
//    private Component toComponent(IComponent iComponent){
//        return Component.builder()
//                .id(iComponent.getId())
//                .name(iComponent.getName())
//                .description(iComponent.getDescription())
//                .inStock(iComponent.getInStock())
//                .minInStock(iComponent.getMinInStock())
//                .updated(iComponent.getUpdated())
//                .user(toUser(iComponent))
//                .productComponents(new HashSet<>(Set.of(toProductComponent(iComponent))))
//                .build();
//    }
//    private User toUser(IComponent iComponent){
//        return User.builder()
//                .id(iComponent.getUserId())
//                .firstName(iComponent.getFirstName())
//                .lastName(iComponent.getLastName())
//                .build();
//    }
//    private ProductComponent toProductComponent(IComponent iComponent){
//        return ProductComponent.builder()
//                .id(iComponent.getProductComponentId())
//                .amount(iComponent.getProductComponentAmount())
//                .product(toProduct(iComponent))
//                .build();
//    }
//
//    private Product toProduct(IComponent iComponent){
//        return Product.builder()
//                .id(iComponent.getProductId())
//                .name(iComponent.getProductName())
//                .build();
//    }
}
