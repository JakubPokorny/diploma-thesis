package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.dto.IContact;
import cz.upce.fei.dt.beckend.entities.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long>, JpaSpecificationExecutor<Contact> {

    @NonNull
    List<Contact> findAll(@NonNull Specification<Contact> specification, @NonNull Sort sort);

    @Query(value = """
            select id, ico, client from contacts
            where lower(ico) like lower(concat('%', :searchTerm, '%'))
            or lower(client) like  lower(concat('%', :searchTerm, '%'))
            or lower(email) like  lower(concat('%', :searchTerm, '%'))
            or lower(phone) like  lower(concat('%', :searchTerm, '%'))
            """, nativeQuery = true)
    @NonNull
    Page<IContact> findAllByIcoOrClientOrEmailOrPhone(@NonNull Pageable pageable, @Param("searchTerm") String searchTerm);

}
