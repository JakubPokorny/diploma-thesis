package cz.upce.fei.dt.backend.repositories;

import cz.upce.fei.dt.backend.entities.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long>, JpaSpecificationExecutor<Status> {

    @Query(value = "select s from Status s where s.status like lower(concat('%', :searchTerm, '%'))")
    Page<Status> findAllByStatus(@NonNull Pageable pageable, @Param("searchTerm") String searchTerm);
}
