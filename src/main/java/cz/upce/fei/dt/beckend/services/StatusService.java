package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.fei.dt.beckend.entities.Deadline;
import cz.upce.fei.dt.beckend.entities.Status;
import cz.upce.fei.dt.beckend.exceptions.UsedStatusException;
import cz.upce.fei.dt.beckend.repositories.StatusRepository;
import cz.upce.fei.dt.beckend.services.filters.StatusFilter;
import cz.upce.fei.dt.beckend.services.specifications.StatusSpec;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class StatusService extends AbstractBackEndDataProvider<Status, StatusFilter> {
    private final StatusRepository statusRepository;
    private final DeadlineService deadlineService;

    @Override
    public Stream<Status> fetchFromBackEnd(Query<Status, StatusFilter> query) {
        Specification<Status> spec = StatusSpec.filterBy(query.getFilter().orElse(null));
        Stream<Status> stream = statusRepository.findAll(spec, VaadinSpringDataHelpers.toSpringDataSort(query)).stream();
        return stream.skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public int sizeInBackEnd(Query<Status, StatusFilter> query) {
        return (int) fetchFromBackEnd(query).count();
    }

    public Stream<Status> findAllByStatus(Query<Status, String> query) {
        String searchTerm = query.getFilter().orElse("");
        return statusRepository.findAllByStatus(VaadinSpringDataHelpers.toSpringPageRequest(query), searchTerm).stream();
    }

    @Transactional
    public void saveStatus(Status status) {
        statusRepository.save(status);
    }

    @Transactional
    public void deleteStatus(Status status) throws UsedStatusException {
        List<Deadline> deadlines = deadlineService.findAllCurrentDeadlinesByStatusID(status.getId());
        if (!deadlines.isEmpty())
            throw new UsedStatusException(deadlines);

        statusRepository.delete(status);
    }
}
