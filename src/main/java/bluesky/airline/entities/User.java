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

// Entity for Users
@Entity
@Table(name = "users")
public class User extends BaseUuidEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String surname;

    @Column(length = 50, unique = true)
    private String username;

    @Column
    private String avatarUrl;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = true, length = 255)
    @JsonIgnore
    private String password;

    @Column(name = "role_code")
    private Integer roleCode;

    // Many-to-Many: a user can have multiple roles and a role can belong to
    // multiple users
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", columnDefinition = "uuid"), inverseJoinColumns = @JoinColumn(name = "role_id", columnDefinition = "uuid"))
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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

    public Integer getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(Integer roleCode) {
        this.roleCode = roleCode;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
