package cz.upce.fei.dt.backend.services;

import cz.upce.fei.dt.backend.entities.Address;
import cz.upce.fei.dt.backend.repositories.AddressRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    @Transactional
    public Address save(Address address) {
        if (address != null)
           return addressRepository.save(address);
        return null;
    }

    @Transactional
    public void delete(Address address) {
        if (address != null)
            addressRepository.delete(address);
    }
}
