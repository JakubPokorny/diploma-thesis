package cz.upce.fei.dt.backend.services;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.fei.dt.backend.entities.Contact;
import cz.upce.fei.dt.backend.exceptions.ResourceNotFoundException;
import cz.upce.fei.dt.backend.repositories.ContactRepository;
import cz.upce.fei.dt.backend.services.filters.ContactFilter;
import cz.upce.fei.dt.backend.services.specifications.ContactSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Stream<Contact> findAllByIcoOrClientOrEmailOrPhone(Query<Contact, String> query) {
        return contactRepository.findAllByIcoOrClientOrEmailOrPhone(PageRequest.of(query.getPage(), query.getPageSize()), query.getFilter().orElse(""))
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
