package study_project.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

// Entità semplice per One-to-One con User
@Entity
@Table(name = "profiles")
public class Profile extends BaseUuidEntity {
    private String bio;

    public UUID getId() { return super.getId(); }
    public void setId(UUID id) { super.setId(id); }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
