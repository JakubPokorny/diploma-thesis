package cz.upce.fei.dt.backend.services;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.backend.dto.IUser;
import cz.upce.fei.dt.backend.entities.Role;
import cz.upce.fei.dt.backend.entities.User;
import cz.upce.fei.dt.backend.exceptions.AuthenticationException;
import cz.upce.fei.dt.backend.exceptions.ResourceNotFoundException;
import cz.upce.fei.dt.backend.repositories.UserRepository;
import cz.upce.fei.dt.backend.services.filters.UserFilter;
import cz.upce.fei.dt.generator.UserGenerator;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private AuthenticationContext authenticationContext;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private DeadlineService deadlineService;
    @Mock
    private CommentService commentService;
    @Mock
    private ComponentService componentService;
    @Mock
    private ContractService contractService;
    @Mock
    private ExtraCostService extraCostService;

    @InjectMocks
    private UserService userService;

    @Mock
    private Query<User, UserFilter> query;

    private static UserFilter userFilter;
    private static List<User> users;

    @BeforeAll
    static void beforeAll() {
        userFilter = new UserFilter();
        users = List.of(
                new User(),
                new User(),
                new User()
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchFromBackEnd() {
        when(query.getFilter()).thenReturn(Optional.of(userFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(10);
        when(userRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(users);

        Stream<User> result = userService.fetchFromBackEnd(query);

        List<User> resultList = result.toList();
        assertEquals(users.size(), resultList.size());

        verify(userRepository).findAll(any(Specification.class), any(Sort.class));
        verify(query, atLeastOnce()).getFilter();
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchFromBackEndWithPaging() {
        when(query.getFilter()).thenReturn(Optional.of(userFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(1);
        when(userRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(users);

        Stream<User> result = userService.fetchFromBackEnd(query);

        List<User> resultList = result.toList();
        assertEquals(1, resultList.size());

        verify(userRepository).findAll(any(Specification.class), any(Sort.class));
        verify(query, atLeastOnce()).getFilter();
    }

    @SuppressWarnings("unchecked")
    @Test
    void sizeInBackEnd() {
        when(query.getFilter()).thenReturn(Optional.of(userFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(10);
        when(userRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(users);

        int result = userService.sizeInBackEnd(query);

        assertEquals(users.size(), result);

        verify(userRepository).findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    void findAllByFirstnameAndLastnameAndEmail() {
        IUser iUser1 = mock(IUser.class);
        when(iUser1.getId()).thenReturn(1L);
        when(iUser1.getFirstName()).thenReturn("Joe");
        when(iUser1.getLastName()).thenReturn("Doe");
        when(iUser1.getEmail()).thenReturn("Joe.Doe@email.com");
        IUser iUser2 = mock(IUser.class);
        when(iUser2.getId()).thenReturn(2L);
        when(iUser2.getFirstName()).thenReturn("Tony");
        when(iUser2.getLastName()).thenReturn("Ezekiel");
        when(iUser2.getEmail()).thenReturn("Tony.Ezekiel@email.com");

        List<IUser> iUsers = List.of(iUser1, iUser2);
        Page<IUser> page = new PageImpl<>(iUsers);

        when(userRepository.findAllByFirstnameAndLastnameAndEmail(any(Pageable.class), anyString())).thenReturn(page);

        Stream<User> result = userService.findAllByFirstnameAndLastnameAndEmail(new Query<>("email.com"));

        List<User> resultList = result.toList();

        User resultContact1 = resultList.getFirst();
        assertEquals(resultContact1.getId(), iUser1.getId());
        assertEquals(resultContact1.getFirstName(), iUser1.getFirstName());
        assertEquals(resultContact1.getLastName(), iUser1.getLastName());
        assertEquals(resultContact1.getEmail(), iUser1.getEmail());

        User resultContact2 = resultList.get(1);
        assertEquals(resultContact2.getId(), iUser2.getId());
        assertEquals(resultContact2.getFirstName(), iUser2.getFirstName());
        assertEquals(resultContact2.getLastName(), iUser2.getLastName());
        assertEquals(resultContact2.getEmail(), iUser2.getEmail());

        verify(userRepository).findAllByFirstnameAndLastnameAndEmail(any(Pageable.class), anyString());
    }

    @Test
    void saveUserThrowsAuthenticationExceptionWhenNoOneIsLogIn() {
        Exception exception = assertThrows(AuthenticationException.class, () -> userService.saveUser(mock(User.class)));
        assertEquals(exception.getMessage(), "Neznámý uživatel. Přihlašte se prosím.");

        verify(authenticationContext).getAuthenticatedUser(User.class);
    }

    @Test
    void saveUserThrowsAuthenticationExceptionWhenUserDoesNotHavePermission() {
        User user = mock(User.class);
        when(user.getRole()).thenReturn(Role.ADMIN);
        when(user.getEmail()).thenReturn("email");

        User authUser = mock(User.class);
        when(authUser.hasRole(Role.ADMIN)).thenReturn(false);
        when(authUser.getRole()).thenReturn(Role.USER);

        User savedUser = mock(User.class);
        when(savedUser.getRole()).thenReturn(Role.USER);

        when(authenticationContext.getAuthenticatedUser(User.class)).thenReturn(Optional.of(authUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(savedUser));

        Exception exception = assertThrows(AuthenticationException.class, () -> userService.saveUser(user));
        assertTrue(exception.getMessage().contains(" nemůže měnit oprávnění"));

        verify(authenticationContext).getAuthenticatedUser(User.class);
        verify(userRepository).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void saveNewUser() throws InterruptedException {
        UI mockUI = mock(UI.class);
        com.vaadin.flow.component.page.Page mockPage = mock(com.vaadin.flow.component.page.Page.class);

        UI.setCurrent(mockUI);
        when(mockUI.getPage()).thenReturn(mockPage);
        doAnswer(_ -> {
                    emailService.send(
                            "email",
                            "Nastav si heslo.",
                            "Pro nastavení hesla přejdi na "
                    );
                    return "";
                }
        ).when(mockPage).fetchCurrentURL(any());

        User user = mock(User.class);
        when(user.getEmail()).thenReturn("email");

        User authUser = mock(User.class);

        when(authenticationContext.getAuthenticatedUser(User.class)).thenReturn(Optional.of(authUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("pass");

        userService.saveUser(user);

        verify(authenticationContext).getAuthenticatedUser(User.class);
        verify(userRepository).findByEmail(anyString());
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
        verify(emailService).send(eq("email"), eq("Nastav si heslo."), eq("Pro nastavení hesla přejdi na "));
    }

    @Test
    void savePersistedUser() {
        User user = mock(User.class);
        when(user.getRole()).thenReturn(Role.ADMIN);
        when(user.getEmail()).thenReturn("email");

        User authUser = mock(User.class);

        User savedUser = mock(User.class);
        when(savedUser.getRole()).thenReturn(Role.ADMIN);

        when(authenticationContext.getAuthenticatedUser(User.class)).thenReturn(Optional.of(authUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(savedUser));

        userService.saveUser(user);

        verify(authenticationContext).getAuthenticatedUser(User.class);
        verify(userRepository).findByEmail(anyString());
        verify(userRepository).save(user);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void deleteUserWhereIdIsNull() {
        userService.deleteUser(null, null);

        verifyNoInteractions(userRepository);
        verifyNoInteractions(deadlineService);
        verifyNoInteractions(commentService);
        verifyNoInteractions(componentService);
    }

    @Test
    void deleteUser() {
        User authUser = mock(User.class);

        User user = UserGenerator.generateUser(1L);
        User alternateUser = UserGenerator.generateUser(2L);

        when(authenticationContext.getAuthenticatedUser(User.class)).thenReturn(Optional.of(authUser));
        when(authUser.getId()).thenReturn(2L);

        userService.deleteUser(user, alternateUser);

        verify(deadlineService).updateAllUserByUser(1L, 2L);
        verify(commentService).updateAllUserByUser(1L, 2L);
        verify(componentService).updateAllUserByUser(1L, 2L);
        verify(contractService).updateAllUserByUser(1L, 2L);
        verify(userRepository).delete(user);
    }

    @Test
    void findByResetTokenThrowsResourceNotFoundExceptions() {
        Exception exception = assertThrows(NotFoundException.class, () -> userService.findByResetToken(""));
        assertEquals(exception.getMessage(), "Neznámý token.");
    }

    @Test
    void findByResetToken() {
        when(userRepository.findByResetToken(anyString())).thenReturn(Optional.of(new User()));

        userService.findByResetToken("token");

        verify(userRepository).findByResetToken(anyString());
    }

    @Test
    void changePassword() {
        User user = mock(User.class);

        when(user.getPassword()).thenReturn("pass");
        when(passwordEncoder.encode(anyString())).thenReturn("pass");

        userService.changePassword(user);

        verify(userRepository).save(user);
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void generateResetTokenThrowsResourceNotFoundException() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("email");
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.generateResetToken(user));
        assertEquals(exception.getMessage(), "neplatný email (email).");
    }

    @Test
    void generateResetToken() throws InterruptedException {
        User savedUser = UserGenerator.generateUser(1L);
        UI mockUI = mock(UI.class);
        UI.setCurrent(mockUI);

        com.vaadin.flow.component.page.Page mockPage = mock(com.vaadin.flow.component.page.Page.class);
        when(mockUI.getPage()).thenReturn(mockPage);
        doAnswer(_ -> {
                    emailService.send(
                            "email",
                            "Nastav si heslo.",
                            "Pro nastavení hesla přejdi na "
                    );
                    return "";
                }
        ).when(mockPage).fetchCurrentURL(any());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(savedUser));

        userService.generateResetToken(savedUser);

        verify(userRepository).findByEmail(anyString());
        verify(userRepository).save(savedUser);
        verify(emailService).send(eq("email"), eq("Nastav si heslo."), eq("Pro nastavení hesla přejdi na "));
    }
}