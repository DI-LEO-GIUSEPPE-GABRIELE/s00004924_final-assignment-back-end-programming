package bluesky.airline.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

// Entity JPA persistita su database (PostgreSQL)
@Entity
@Table(name = "users")
public class User extends BaseUuidEntity {

    @Column(nullable = false, length = 50) // Serve per indicare che il campo non può essere nullo e ha una lunghezza
                                           // massima di 50 caratteri
    private String name;

    @Column(nullable = false, unique = true, length = 255) // Serve per indicare che il campo non può essere nullo, deve
                                                           // essere unico e ha una lunghezza massima di 255 caratteri
    private String email;

    @Column(nullable = true, length = 255)
    private String password;

    // Rimosse relazioni demo (Profile, Order)

    // Many-to-Many: un utente può avere più ruoli e un ruolo può appartenere a più
    // utenti
    // Owner: User definisce la tabella di join user_roles
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

    // Getter/Setter per relationships

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
