package cz.upce.fei.dt.backend.services;

import com.vaadin.flow.data.provider.Query;
import cz.upce.fei.dt.backend.entities.Deadline;
import cz.upce.fei.dt.backend.entities.Status;
import cz.upce.fei.dt.backend.repositories.StatusRepository;
import cz.upce.fei.dt.backend.services.filters.StatusFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusServiceTest {
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private DeadlineService deadlineService;

    @InjectMocks
    private StatusService statusService;

    @Mock
    private Query<Status, StatusFilter> query;

    private static StatusFilter statusFilter;
    private static List<Status> statuses;

    @BeforeAll
    static void beforeAll() {
        statusFilter = new StatusFilter();
        statuses = List.of(
                new Status(),
                new Status(),
                new Status()
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchFromBackEnd() {
        when(query.getFilter()).thenReturn(Optional.of(statusFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(10);
        when(statusRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(statuses);

        Stream<Status> result = statusService.fetchFromBackEnd(query);

        List<Status> resultList = result.toList();
        assertEquals(resultList.size(), statuses.size());

        verify(statusRepository).findAll(any(Specification.class), any(Sort.class));
        verify(query, atLeastOnce()).getFilter();
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchFromBackEndWithPaging() {
        when(query.getFilter()).thenReturn(Optional.of(statusFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(1);
        when(statusRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(statuses);

        Stream<Status> result = statusService.fetchFromBackEnd(query);

        List<Status> resultList = result.toList();
        assertEquals(resultList.size(), 1);

        verify(statusRepository).findAll(any(Specification.class), any(Sort.class));
        verify(query, atLeastOnce()).getFilter();
    }

    @SuppressWarnings("unchecked")
    @Test
    void sizeInBackEnd() {
        when(query.getFilter()).thenReturn(Optional.of(statusFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(10);
        when(statusRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(statuses);

        int result = statusService.sizeInBackEnd(query);

        assertEquals(statuses.size(), result);

        verify(statusRepository).findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    void findAllByStatus() {
        when(statusRepository.findAllByStatus(any(), anyString())).thenReturn(new PageImpl<>(statuses));

        Stream<Status> result = statusService.findAllByStatus(new Query<>("Done"));

        List<Status> resultList = result.toList();
        assertEquals(resultList.size(), statuses.size());

        verify(statusRepository).findAllByStatus(any(), anyString());
    }

    @Test
    void saveStatus() {
        Status status = new Status();

        statusService.saveStatus(status);

        verify(statusRepository).save(status);
    }

    @Test
    void deleteStatusThrowsExceptionWhenDeadlineIsCurrentlyUsed() {
        Status status = Status.builder().id(1L).build();
        List<Deadline> deadlines = List.of(new Deadline());

        when(deadlineService.findAllCurrentDeadlinesByStatusId(anyLong())).thenReturn(deadlines);

        assertThrows(Exception.class, () -> statusService.deleteStatus(status));

        verify(deadlineService).findAllCurrentDeadlinesByStatusId(anyLong());
        verifyNoInteractions(statusRepository);
    }

    @Test
    void deleteStatus() {
        Status status = Status.builder().id(1L).build();
        when(deadlineService.findAllCurrentDeadlinesByStatusId(anyLong())).thenReturn(Collections.emptyList());

        statusService.deleteStatus(status);

        verify(deadlineService).findAllCurrentDeadlinesByStatusId(anyLong());
        verify(statusRepository).delete(status);
    }
}