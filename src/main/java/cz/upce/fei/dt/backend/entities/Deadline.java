package cz.upce.fei.dt.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "deadlines")
public class Deadline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDate deadline;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime created;

    @Column
    @UpdateTimestamp
    private LocalDateTime updated;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    public boolean isWithoutDeadline() {
        return deadline == null;
    }

    public boolean isBeforeOrNowDeadline() {
        if (deadline == null)
            return false;
        return LocalDate.now().isBefore(deadline) || LocalDate.now().isEqual(deadline);
    }

    public boolean isAfterDeadline() {
        if (deadline == null)
            return false;
        return LocalDate.now().isAfter(deadline);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deadline deadline1 = (Deadline) o;
        return Objects.equals(id, deadline1.id) && status == deadline1.status && Objects.equals(deadline, deadline1.deadline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, deadline);
    }
}
