package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.data.provider.Query;
import cz.upce.fei.dt.beckend.dto.IContact;
import cz.upce.fei.dt.beckend.entities.Address;
import cz.upce.fei.dt.beckend.entities.Contact;
import cz.upce.fei.dt.beckend.exceptions.ResourceNotFoundException;
import cz.upce.fei.dt.beckend.repositories.ContactRepository;
import cz.upce.fei.dt.beckend.services.filters.ContactFilter;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {
    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    @Mock
    private Query<Contact, ContactFilter> query;

    private static ContactFilter contactFilter;
    private static List<Contact> contacts;

    @BeforeAll
    static void beforeAll() {
        contactFilter = new ContactFilter();
        contacts = List.of(
                new Contact(),
                new Contact(),
                new Contact()
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchFromBackEnd() {
        when(query.getFilter()).thenReturn(Optional.of(contactFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(10);
        when(contactRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(contacts);

        Stream<Contact> result = contactService.fetchFromBackEnd(query);

        List<Contact> resultList = result.toList();
        assertEquals(contacts.size(), resultList.size());

        verify(contactRepository).findAll(any(Specification.class), any(Sort.class));
        verify(query, atLeastOnce()).getFilter();
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchFromBackEndWithPaging() {
        when(query.getFilter()).thenReturn(Optional.of(contactFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(1);
        when(contactRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(contacts);

        Stream<Contact> result = contactService.fetchFromBackEnd(query);

        List<Contact> resultList = result.toList();
        assertEquals(1, resultList.size());

        verify(contactRepository).findAll(any(Specification.class), any(Sort.class));
        verify(query, atLeastOnce()).getFilter();
    }

    @SuppressWarnings("unchecked")
    @Test
    void sizeInBackEnd() {
        when(query.getFilter()).thenReturn(Optional.of(contactFilter));
        when(query.getOffset()).thenReturn(0);
        when(query.getLimit()).thenReturn(10);
        when(contactRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(contacts);

        int result = contactService.sizeInBackEnd(query);

        assertEquals(contacts.size(), result);

        verify(contactRepository).findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    void findAllByIcoOrClientOrEmailOrPhone() {
        IContact iContact1 = mock(IContact.class);
        when(iContact1.getId()).thenReturn(1L);
        when(iContact1.getICO()).thenReturn("ICO 1");
        when(iContact1.getClient()).thenReturn("Client 1");
        IContact iContact2 = mock(IContact.class);
        when(iContact2.getId()).thenReturn(2L);
        when(iContact2.getICO()).thenReturn("ICO 2");
        when(iContact2.getClient()).thenReturn("Client 2");

        List<IContact> iContacts = List.of(iContact1, iContact2);
        Page<IContact> page = new PageImpl<>(iContacts);

        when(contactRepository.findAllByIcoOrClientOrEmailOrPhone(any(Pageable.class), anyString())).thenReturn(page);

        Stream<Contact> result = contactService.findAllByIcoOrClientOrEmailOrPhone(new Query<>("Client"));
        List<Contact> resultList = result.toList();

        Contact resultContact1 = resultList.getFirst();
        assertEquals(resultContact1.getId(), iContact1.getId());
        assertEquals(resultContact1.getICO(), iContact1.getICO());
        assertEquals(resultContact1.getClient(), iContact1.getClient());

        Contact resultContact2 = resultList.get(1);
        assertEquals(resultContact2.getId(), iContact2.getId());
        assertEquals(resultContact2.getICO(), iContact2.getICO());
        assertEquals(resultContact2.getClient(), iContact2.getClient());

        verify(contactRepository).findAllByIcoOrClientOrEmailOrPhone(any(Pageable.class), anyString());
    }

    @Test
    void findByIdThrowsResourceNotFoundException() {
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> contactService.findById(anyLong()));
        assertEquals(exception.getMessage(), "Klient nenalezen.");
    }

    @Test
    void findById() {
        Contact contact = new Contact();
        when(contactRepository.findById(anyLong())).thenReturn(Optional.of(contact));

        Contact result = contactService.findById(anyLong());

        assertEquals(contact, result);

        verify(contactRepository).findById(anyLong());
    }

    @Test
    void saveContactWithoutDeliveryAddress() {
        Contact contact = mock(Contact.class);

        when(contact.hasDeliveryAddress()).thenReturn(false);

        contactService.saveContact(contact);

        assertNull(contact.getDeliveryAddress());

        verify(contactRepository).save(contact);
    }

    @Test
    void saveContact() {
        Contact contact = mock(Contact.class);
        Address address = mock(Address.class);

        when(contact.hasDeliveryAddress()).thenReturn(true);
        when(contact.getDeliveryAddress()).thenReturn(address);

        contactService.saveContact(contact);

        assertNotNull(contact.getDeliveryAddress());

        verify(contactRepository).save(contact);
    }

    @Test
    void deleteContact() {
        Contact contact = mock(Contact.class);

        contactService.deleteContact(contact);

        verify(contactRepository).delete(contact);
    }
}