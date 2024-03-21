package cz.upce.fei.dt.beckend.services;

import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.ProductComponent;
import cz.upce.fei.dt.beckend.repositories.ComponentRepository;
import cz.upce.fei.dt.beckend.repositories.ProductComponentRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ComponentService {
    private final ComponentRepository componentRepository;
    private final ProductComponentRepository productComponentRepository;

    public List<Component> findAllComponentsIdAndName() {
        //todo add padding
        List<Component> components = new ArrayList<>();
        componentRepository.findAllComponentsIDAndName().forEach(
                projection -> components.add(
                        Component.builder()
                                .id(projection.getId())
                                .name(projection.getName())
                                .build())
        );
        return components;
    }
    public Stream<Component> findAll(int page, int pageSize){
        return componentRepository.findAll(PageRequest.of(page, pageSize)).stream();
    }
    @Transactional
    public void saveComponent(Component component){
        component.setUpdated(LocalDateTime.now());
        if (component.getId() == null){
            List<ProductComponent> productComponents = component.getProductComponents();
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
                .orElseThrow(()-> new Exception("Komponenta "+ component.getName() +" nenalezana."));
        componentRepository.delete(wantToDelete);
    }

}
