package cz.upce.fei.dt.beckend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Builder
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "statuses")
public class Status {
    @AllArgsConstructor
    @Getter
    public enum Theme{
        SUCCESS("success", "hotovo"),
        CONTRAST("contrast", "čeká na akci"),
        PENDING("", "v procesu"),
        WARNING("warning", "varování"),
        ERROR("error", "chyba");

        private final String theme;
        private final String meaning;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String status;

    @Column
    @Enumerated(EnumType.STRING)
    private Theme theme ;

    @OneToMany(mappedBy = "status", cascade = CascadeType.REMOVE)
    @JsonIgnore
    @ToString.Exclude
    Set<Deadline> deadlines;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status1 = (Status) o;
        return Objects.equals(id, status1.id) && Objects.equals(status, status1.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status);
    }
}
