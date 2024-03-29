package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.dto.IContact;
import cz.upce.fei.dt.beckend.entities.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query(value = "select id, ico, name from contacts " +
            "where lower(ico) like lower(concat('%', :searchTerm, '%'))" +
            "or lower(name) like  lower(concat('%', :searchTerm, '%'))", nativeQuery = true)
    @NonNull
    Page<IContact> findAllContactsIDAndICOAndName(@NonNull Pageable pageable, @Param("searchTerm") String searchTerm);
}
