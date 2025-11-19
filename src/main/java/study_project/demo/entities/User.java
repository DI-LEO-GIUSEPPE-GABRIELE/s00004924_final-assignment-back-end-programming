package study_project.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

// Entity JPA persistita su database (PostgreSQL)
@Entity
@Table(name = "users")
public class User {
    @Id // Serve per indicare la chiave primaria dell'entità
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Serve per generare automaticamente il valore della chiave
                                                        // primaria
    private Long id;

    @Column(nullable = false, length = 50) // Serve per indicare che il campo non può essere nullo e ha una lunghezza
                                           // massima di 50 caratteri
    private String name;

    @Column(nullable = false, unique = true, length = 255) // Serve per indicare che il campo non può essere nullo, deve
                                                           // essere unico e ha una lunghezza massima di 255 caratteri
    private String email;

    public User() {
    }

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
