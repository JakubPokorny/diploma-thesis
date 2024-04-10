package cz.upce.fei.dt.beckend.services;

import cz.upce.fei.dt.beckend.entities.Contact;
import cz.upce.fei.dt.beckend.repositories.AddressRepository;
import cz.upce.fei.dt.beckend.repositories.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;

    public Stream<Contact> findAllContactsIdAndICOAndName(int page, int pageSize, String searchTerm) {
        return contactRepository.findAllContactsIDAndICOAndName(PageRequest.of(page, pageSize), searchTerm)
                .stream()
                .map(iContact -> Contact.builder()
                        .id(iContact.getId())
                        .ICO(iContact.getICO())
                        .client(iContact.getName())
                        .build()
                );
    }

    public List<Contact> getAll() {
        return contactRepository.findAll();
    }

    public void saveContact(Contact contact) {
        if (!contact.hasDeliveryAddress())
            contact.setDeliveryAddress(null);
        contactRepository.save(contact);
    }

    public void deleteContact(Contact contact) {
        contactRepository.delete(contact);
    }
}
