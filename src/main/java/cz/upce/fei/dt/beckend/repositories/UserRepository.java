package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.dto.IUser;
import cz.upce.fei.dt.beckend.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    @Query("""
            select
             u.id as id,
             u.firstName as firstName,
             u.lastName as lastName,
             u.email as email
            from User u
            where lower(u.firstName) like lower(concat('%', :searchTerm, '%'))
            or lower(u.lastName) like lower(concat('%', :searchTerm, '%'))
            or lower(u.email) like lower(concat('%', :searchTerm, '%'))
            """)
    Page<IUser> findAllByFirstnameAndLastnameAndEmail(@NonNull Pageable pageable, @Param("searchTerm") String searchTerm);
}
