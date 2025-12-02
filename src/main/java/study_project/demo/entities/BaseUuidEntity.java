package study_project.demo.entities;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Id;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

// Base astratta per ID UUID: condivisa da tutte le entity concrete
@MappedSuperclass
public abstract class BaseUuidEntity {
    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
}

