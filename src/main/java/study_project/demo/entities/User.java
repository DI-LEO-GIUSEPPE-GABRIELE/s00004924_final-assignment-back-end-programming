package study_project.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
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

    // One-to-One: un utente ha un singolo profilo
    // Owner della relazione: User (possiede la FK profile_id)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", columnDefinition = "uuid")
    @JsonIgnore // evitiamo cicli/inizializzazioni lazy nella serializzazione delle API
    private Profile profile;

    // One-to-Many: un utente ha molti ordini
    // Lato inverso: mappedBy "user" perché la FK è nella tabella orders
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();

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

    // Getter/Setter per relationships
    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
