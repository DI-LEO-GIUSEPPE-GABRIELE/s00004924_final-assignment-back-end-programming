package bluesky.airline.entities;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;
import java.util.UUID;

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
