package bluesky.airline.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

// Entity for Compartments
@Entity
@Table(name = "compartments")
public class Compartment extends BaseUuidEntity {
    @Column(name = "compartment_code", nullable = false, unique = true)
    private String compartmentCode;

    @Column(name = "description")
    private String description;

    public String getCompartmentCode() {
        return compartmentCode;
    }

    public void setCompartmentCode(String compartmentCode) {
        this.compartmentCode = compartmentCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
