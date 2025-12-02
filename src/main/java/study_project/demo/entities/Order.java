package study_project.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import java.util.UUID;

// Entità "Order": lato owner della One-to-Many
@Entity
@Table(name = "orders")
public class Order extends BaseUuidEntity {
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "uuid")
    private User user;

    public UUID getId() { return super.getId(); }
    public void setId(UUID id) { super.setId(id); }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
