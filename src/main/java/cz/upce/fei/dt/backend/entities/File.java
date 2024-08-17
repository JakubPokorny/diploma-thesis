package cz.upce.fei.dt.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 50, nullable = false)
    @NotBlank(message = "Název souboru je povinný.")
    @Size(max = 50, message = "Název souboru je příliš dlouhý, max 50 znaků.")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Cesta k souboru je povinná.")
    @Size(max = 255, message = "Cesta k souboru je příliš dlouhá, max 255 znaků.")
    private String path;

    @Column(nullable = false)
    private Long size;

    @Column(length = 100, nullable = false)
    @NotBlank(message = "Typ souboru je povinný.")
    @Size(max = 100, message = "Typ souboru je příliš dlouhý, max 100 znaků.")
    private String type;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return id == file.id && Objects.equals(name, file.name) && Objects.equals(path, file.path) && Objects.equals(size, file.size) && Objects.equals(type, file.type) && Objects.equals(created, file.created) && Objects.equals(contract, file.contract);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, path, size, type, created, contract);
    }
}
