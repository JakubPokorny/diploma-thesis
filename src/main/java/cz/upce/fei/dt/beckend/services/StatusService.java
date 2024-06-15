package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.fei.dt.beckend.entities.Status;
import cz.upce.fei.dt.beckend.repositories.StatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class StatusService {
    private final StatusRepository statusRepository;

    public Stream<Status> findAllByStatus(Query<Status, String> query) {
        String searchTerm = query.getFilter().orElse("");
        return statusRepository.findAllByStatus(VaadinSpringDataHelpers.toSpringPageRequest(query), searchTerm).stream();
    }
}
