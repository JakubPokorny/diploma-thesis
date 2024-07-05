package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.entities.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    @Query("select f from File f where f.contract.id = :contractId")
    @NonNull
    Page<File> findAllByContractId(@NonNull @Param("contractId") Long contractId, @NonNull Pageable pageable);

    List<File> findAllByContractId(Long contractId);
}
