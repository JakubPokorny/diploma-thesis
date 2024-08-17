package cz.upce.fei.dt.backend.services;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.fei.dt.backend.dto.CheckStockDto;
import cz.upce.fei.dt.backend.dto.ComponentMetrics;
import cz.upce.fei.dt.backend.dto.IComponentCount;
import cz.upce.fei.dt.backend.entities.Component;
import cz.upce.fei.dt.backend.entities.ProductComponent;
import cz.upce.fei.dt.backend.repositories.ComponentRepository;
import cz.upce.fei.dt.backend.services.filters.ComponentFilter;
import cz.upce.fei.dt.backend.services.specifications.ComponentSpec;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ComponentService extends AbstractBackEndDataProvider<Component, ComponentFilter> {
    private final ComponentRepository componentRepository;
    private final ProductComponentService productComponentService;
    private final ProductService productService;
    private final EmailService emailService;

    @Override
    public Stream<Component> fetchFromBackEnd(Query<Component, ComponentFilter> query) {
        Specification<Component> spec = ComponentSpec.filterBy(query.getFilter().orElse(new ComponentFilter()));
        Stream<Component> stream = componentRepository.findAll(spec, VaadinSpringDataHelpers.toSpringDataSort(query))
                .stream();

        if (query.getFilter().isPresent()) {
            stream = stream.filter(component -> query.getFilter().get().filter(component));
        }

        return stream.skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public int sizeInBackEnd(Query<Component, ComponentFilter> query) {
        return (int) fetchFromBackEnd(query).count();
    }

    public Stream<Component> findAllByName(Query<Component, String> query) {
        return componentRepository.findAllByName(PageRequest.of(query.getPage(), query.getPageSize()), query.getFilter().orElse(""))
                .stream()
                .map(iComponent -> Component.builder()
                        .id(iComponent.getId())
                        .name(iComponent.getName())
                        .price(iComponent.getPrice())
                        .build()
                );
    }

    @Transactional
    public void saveComponent(Component component) {
        component.setUpdated(LocalDateTime.now());
        if (component.getId() == null) {
            Set<ProductComponent> productComponents = component.getProductComponents();
            component.setProductComponents(null);
            component = componentRepository.save(component);

            for (ProductComponent productComponent : productComponents) {
                productComponent.setComponent(component);
            }
            component.setProductComponents(productComponents);
        }

        List<ProductComponent> orphans = productComponentService.findAllByComponentId(component.getId());
        orphans.removeAll(component.getProductComponents());
        productComponentService.deleteAll(orphans);

        componentRepository.save(component);

        Stream<Long> orphanIDs = orphans
                .stream()
                .map(productComponent -> productComponent.getId().getProductId());
        Stream<Long> productComponentsIDs = component.getProductComponents()
                .stream()
                .map(productComponent -> productComponent.getId().getProductId());
        productService.updatePrices(Stream.concat(orphanIDs, productComponentsIDs).collect(Collectors.toSet()));

    }

    @Transactional
    public void deleteComponent(Component component) {
        componentRepository.delete(component);
        productService.updatePrices(productService.findAllByComponent(component));
    }

    @Transactional
    public void updateAll(Collection<CheckStockDto> componentsToUpdate) {
        List<CheckStockDto> underMinInStockLimit = new ArrayList<>();
        componentsToUpdate.forEach(checkStockDto -> {
            componentRepository.updateAmountById(checkStockDto.getComponentID(), checkStockDto.getComponentsInStock());

            if (checkStockDto.sendNotification())
                underMinInStockLimit.add(checkStockDto);
        });

        emailService.sendStockNotification(underMinInStockLimit);
    }

    @Transactional
    public void updateAllUserByUser(Long userId, Long alternateUserId) {
        componentRepository.updateAllUserByUser(userId, alternateUserId);
    }

    public ComponentMetrics countMetrics() {
        int all = 0, inStockCounter = 0, supply = 0, missing = 0;
        for (IComponentCount component : componentRepository.findAllComponentMetrics()) {
            int minInStock = component.getMinInStock();
            int inStock = component.getInStock();

            all++;
            if (inStock > minInStock && inStock > 0) inStockCounter++;
            if (inStock < 0) missing++;
            if (inStock <= minInStock && inStock >= 0) supply++;
        }
        return new ComponentMetrics(all, inStockCounter, supply, missing);
    }
}
