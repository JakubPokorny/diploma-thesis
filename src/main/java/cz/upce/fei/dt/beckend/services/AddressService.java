package cz.upce.fei.dt.beckend.services;

import cz.upce.fei.dt.beckend.entities.Address;
import cz.upce.fei.dt.beckend.repositories.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    public void saveAddress(Address address) {
        System.out.println(address.toString());
    }
}
