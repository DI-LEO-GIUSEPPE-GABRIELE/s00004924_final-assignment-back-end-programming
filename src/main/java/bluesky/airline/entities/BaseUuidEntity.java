package bluesky.airline.entities;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;

// Entity for Base UUID IDs
@MappedSuperclass
public abstract class BaseUuidEntity {
    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
