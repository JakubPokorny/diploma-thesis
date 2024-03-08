package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
