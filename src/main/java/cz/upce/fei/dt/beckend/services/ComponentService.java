package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ComponentService extends AbstractBackEndDataProvider<Component, ComponentFilter> {
    private final ComponentRepository componentRepository;
    private final ProductComponentRepository productComponentRepository;
    private final EmailService emailService;

    @Override
    public Stream<Component> fetchFromBackEnd(Query<Component, ComponentFilter> query) {
        Specification<Component> spec = ComponentSpec.filterBy(query.getFilter().orElse(new ComponentFilter()));
        Stream<Component> stream = componentRepository.findAll(spec, VaadinSpringDataHelpers.toSpringDataSort(query)).stream();

        if (query.getFilter().isPresent()) {
            stream = stream.filter(contract -> query.getFilter().get().filter(contract));
        }

        return stream.skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public int sizeInBackEnd(Query<Component, ComponentFilter> query) {
        return (int) fetchFromBackEnd(query).count();
    }

    public Stream<Component> findAllByName(int page, int pageSize, String searchTerm) {
        return componentRepository.findAllByName(PageRequest.of(page, pageSize), searchTerm)
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
    public void updateAll(Collection<CheckStockDto> componentsToUpdate) {
        List<CheckStockDto> underMinInStockLimit = new ArrayList<>();
        componentsToUpdate.forEach(checkStockDto -> {
            componentRepository.updateAmountById(checkStockDto.getComponentID(), checkStockDto.getComponentsInStock());

            if (checkStockDto.getMinComponentsInStock() != null && checkStockDto.getComponentsInStock() < checkStockDto.getMinComponentsInStock() && checkStockDto.getEmail() != null) {
                underMinInStockLimit.add(checkStockDto);
            }
        });

        emailService.sendStockNotification(underMinInStockLimit);
    }


    public int getCountAll() {
        return componentRepository.countAll();
    }

    public int getCountInStock() {
        return componentRepository.countInStock();
    }

    public int getCountInStockSupply() {
        return componentRepository.countSupply();
    }

    public int getCountInStockMissing() {
        return componentRepository.countMissing();
    }


}
