package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.dto.INote;
import cz.upce.fei.dt.beckend.entities.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    @Query("""
            select
             n.note as note,
             n.created as created,
             n.user.firstName as firstName,
             n.user.lastName as lastName
            from Note n where n.contract.id = :id
            """)
    @NonNull
    Page<INote> findAllByContractId(@NonNull Pageable pageable, @NonNull @Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "update Note n set n.user.id = :alternateUserId where n.user.id = :userId")
    void updateAllUserByUser(@NonNull @Param("userId") Long userId, @NonNull @Param("alternateUserId") Long alternateUserId);
}
