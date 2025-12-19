package bluesky.airline.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.util.UUID;

// Entity for Roles
@Entity
@Table(name = "roles")
public class Role extends BaseUuidEntity {
    @Column(name = "role_name", nullable = false, unique = true)
    private String name;

    public UUID getId() {
        return super.getId();
    }

    public void setId(UUID id) {
        super.setId(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
