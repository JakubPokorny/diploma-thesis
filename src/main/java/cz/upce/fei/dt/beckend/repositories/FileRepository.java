package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.dto.IFile;
import cz.upce.fei.dt.beckend.entities.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    @Query("select f from File f where f.contract.id = ?1")
    @NonNull
    Page<IFile> findAllByContractId(Long id, @NonNull Pageable pageable);
}
