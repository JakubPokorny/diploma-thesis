package cz.upce.fei.dt.backend.repositories;

import cz.upce.fei.dt.backend.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
