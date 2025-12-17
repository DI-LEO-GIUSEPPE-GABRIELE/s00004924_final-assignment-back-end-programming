package bluesky.airline.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;

// JPA entity persisted on database (PostgreSQL)
@Entity
@Table(name = "users")
public class User extends BaseUuidEntity {

    @Column(nullable = false, length = 50) // Non-null field with max length 50
    private String name;

    @Column(nullable = false, unique = true, length = 255) // Non-null, unique, max length 255
    private String email;

    @Column(nullable = true, length = 255)
    @JsonIgnore
    private String password;

    // Removed demo relationships (Profile, Order)

    // Many-to-Many: a user can have multiple roles and a role can belong to
    // multiple users
    // Owner: User defines the join table user_roles
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", columnDefinition = "uuid"), inverseJoinColumns = @JoinColumn(name = "role_id", columnDefinition = "uuid"))
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(UUID id, String name, String email) {
        this.setId(id);
        this.name = name;
        this.email = email;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getters/Setters for relationships

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
