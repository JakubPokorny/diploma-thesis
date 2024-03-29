package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.entities.Deadline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeadlineRepository extends JpaRepository<Deadline, Long> {
}
