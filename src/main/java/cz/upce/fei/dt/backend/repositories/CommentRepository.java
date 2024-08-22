package cz.upce.fei.dt.backend.repositories;

import cz.upce.fei.dt.backend.dto.IComment;
import cz.upce.fei.dt.backend.entities.Comment;
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
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
            select
             c.comment as comment,
             c.created as created,
             c.user.firstName as firstName,
             c.user.lastName as lastName
            from Comment c where c.contract.id = :id
            """)
    @NonNull
    Page<IComment> findAllByContractId(@NonNull Pageable pageable, @NonNull @Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "update Comment c set c.user.id = :alternateUserId where c.user.id = :userId")
    void updateAllUserByUser(@NonNull @Param("userId") Long userId, @NonNull @Param("alternateUserId") Long alternateUserId);
}
