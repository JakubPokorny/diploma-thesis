package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.fei.dt.beckend.entities.Contact;
import cz.upce.fei.dt.beckend.exceptions.ResourceNotFoundException;
import cz.upce.fei.dt.beckend.repositories.ContactRepository;
import cz.upce.fei.dt.beckend.services.filters.ContactFilter;
import cz.upce.fei.dt.beckend.services.specifications.ContactSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ContactService extends AbstractBackEndDataProvider<Contact, ContactFilter> {
    private final ContactRepository contactRepository;

    @Override
    public Stream<Contact> fetchFromBackEnd(Query<Contact, ContactFilter> query) {
        Specification<Contact> spec = ContactSpec.filterBy(query.getFilter().orElse(new ContactFilter()));
        Stream<Contact> stream = contactRepository.findAll(spec, VaadinSpringDataHelpers.toSpringDataSort(query)).stream();

        return stream.skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public int sizeInBackEnd(Query<Contact, ContactFilter> query) {
        return (int) fetchFromBackEnd(query).count();
    }

    public Stream<Contact> findAllByIcoOrClientOrEmailOrPhone(int page, int pageSize, String searchTerm) {
        return contactRepository.findAllByIcoOrClientOrEmailOrPhone(PageRequest.of(page, pageSize), searchTerm)
                .stream()
                .map(iContact -> Contact.builder()
                        .id(iContact.getId())
                        .ICO(iContact.getICO())
                        .client(iContact.getClient())
                        .build()
                );
    }

    public Contact findById(Long id) throws ResourceNotFoundException {
        return contactRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Klient nenalezen."));
    }

    public List<Contact> getAll() {
        return contactRepository.findAll();
    }

    @Transactional
    public void saveContact(Contact contact) {
        if (!contact.hasDeliveryAddress())
            contact.setDeliveryAddress(null);
        contactRepository.save(contact);
    }

    @Transactional
    public void deleteContact(Contact contact) {
        contactRepository.delete(contact);
    }
}
