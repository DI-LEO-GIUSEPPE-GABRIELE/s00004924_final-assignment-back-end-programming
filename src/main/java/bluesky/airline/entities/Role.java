package bluesky.airline.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

// Entità "Role" usata nel Many-to-Many con User
@Entity
@Table(name = "roles")
public class Role extends BaseUuidEntity {
    private String name;

    public UUID getId() { return super.getId(); }
    public void setId(UUID id) { super.setId(id); }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
