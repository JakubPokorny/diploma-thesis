package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.beckend.dto.IDeadline;
import cz.upce.fei.dt.beckend.entities.Deadline;
import cz.upce.fei.dt.beckend.entities.Status;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.exceptions.AuthenticationException;
import cz.upce.fei.dt.beckend.repositories.DeadlineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeadlineServiceTest {
    @Mock
    private DeadlineRepository deadlineRepository;
    @Mock
    private AuthenticationContext authenticationContext;

    @InjectMocks
    private DeadlineService deadlineService;

    @Captor
    private ArgumentCaptor<Deadline> captor;

    @Test
    void saveThrowsAuthenticationExceptionWhenNoOneIsAuthenticated() {
        Deadline deadline = mock(Deadline.class);
        when(deadline.getId()).thenReturn(1L);
        when(deadlineRepository.findById(anyLong())).thenReturn(Optional.of(mock(Deadline.class)));

        Exception exception = assertThrows(AuthenticationException.class, () -> deadlineService.save(deadline));
        assertEquals(exception.getMessage(), "Neznámý uživatel. Přihlašte se prosím.");

        verify(deadlineRepository).findById(1L);
        verify(authenticationContext).getAuthenticatedUser(User.class);
        verify(deadlineRepository, never()).save(any(Deadline.class));
    }

    @Test
    void saveNoChanges() {
        Deadline deadline = mock(Deadline.class);
        when(deadline.getId()).thenReturn(1L);
        when(deadlineRepository.findById(anyLong())).thenReturn(Optional.of(deadline));

        deadlineService.save(deadline);

        verifyNoInteractions(authenticationContext);
        verify(deadlineRepository, never()).save(any());
    }


    @Test
    void saveNewDeadline() {
        Deadline deadline = Deadline.builder().id(1L).build();
        when(deadlineRepository.findById(anyLong())).thenReturn(Optional.of(mock(Deadline.class)));

        User user = mock(User.class);
        when(authenticationContext.getAuthenticatedUser(User.class)).thenReturn(Optional.of(user));

        deadlineService.save(deadline);

        verify(deadlineRepository).save(captor.capture());
        assertEquals(user, captor.getValue().getUser());
        assertNull(captor.getValue().getId());
        verify(authenticationContext).getAuthenticatedUser(User.class);
        verify(deadlineRepository).findById(1L);
    }

    @Test
    void deleteAll() {
        deadlineService.deleteAll(1L);
        verify(deadlineRepository).deleteAllByContractId(1L);
    }

    @Test
    void findAllByContractId() {
        IDeadline iDeadline = mock(IDeadline.class);
        when(iDeadline.getId()).thenReturn(1L);
        when(iDeadline.getStatus()).thenReturn(mock(Status.class));
        when(iDeadline.getDeadline()).thenReturn(LocalDate.now());
        when(iDeadline.getCreated()).thenReturn(LocalDateTime.now());
        when(iDeadline.getFirstName()).thenReturn("John");
        when(iDeadline.getLastName()).thenReturn("Doe");

        List<IDeadline> iDeadlines = List.of(iDeadline);
        Page<IDeadline> page = new PageImpl<>(iDeadlines);

        when(deadlineRepository.findAllByContractId(any(), anyLong())).thenReturn(page);

        Stream<Deadline> result = deadlineService.findAllByContractId(1L, new Query<>());

        List<Deadline> deadlines = result.toList();

        Deadline deadline = deadlines.getFirst();
        assertEquals(deadline.getId(), iDeadline.getId());
        assertEquals(deadline.getStatus(), iDeadline.getStatus());
        assertEquals(deadline.getDeadline(), iDeadline.getDeadline());
        assertEquals(deadline.getCreated(), iDeadline.getCreated());
        assertEquals(deadline.getUser().getFirstName(), iDeadline.getFirstName());
        assertEquals(deadline.getUser().getLastName(), iDeadline.getLastName());

        verify(deadlineRepository).findAllByContractId(any(), eq(1L));
    }

    @Test
    void findFirstByContractIdOrderByCreatedDesc() {
        deadlineService.findFirstByContractIdOrderByCreatedDesc(1L);
        verify(deadlineRepository).findFirstByContractIdOrderByCreatedDesc(eq(1L));
    }

    @Test
    void findAllCurrentDeadlinesByStatusId() {
        deadlineService.findAllCurrentDeadlinesByStatusId(1L);
        verify(deadlineRepository).findAllCurrentDeadlinesByStatusId(eq(1L));
    }

    @Test
    void findAllCurrentDeadlines() {
        deadlineService.findAllCurrentDeadlines();
        verify(deadlineRepository).findAllCurrentDeadlines();
    }

    @Test
    void updateAllUserByUser(){
        deadlineService.updateAllUserByUser(1L, 2L);
        verify(deadlineRepository).updateAllUserByUser(1L, 2L);
    }
}