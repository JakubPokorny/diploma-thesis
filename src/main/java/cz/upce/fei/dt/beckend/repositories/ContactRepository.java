package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
}
