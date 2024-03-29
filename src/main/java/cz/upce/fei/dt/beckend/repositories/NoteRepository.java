package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.dto.INote;
import cz.upce.fei.dt.beckend.entities.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    @Query("select n from Note n where n.contract.id = ?1")
    @NonNull
    Page<INote> findAllByContractId(Long id, @NonNull Pageable pageable);
}
