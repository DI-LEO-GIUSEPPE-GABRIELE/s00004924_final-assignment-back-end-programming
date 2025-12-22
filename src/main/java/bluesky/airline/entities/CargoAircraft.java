package bluesky.airline.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// Entity for Cargo Aircrafts
@Entity
@DiscriminatorValue("CARGO")
public class CargoAircraft extends Aircraft {
    @Column(name = "max_load_capacity")
    private Integer maxLoadCapacity;

    public Integer getMaxLoadCapacity() {
        return maxLoadCapacity;
    }

    public void setMaxLoadCapacity(Integer maxLoadCapacity) {
        this.maxLoadCapacity = maxLoadCapacity;
    }
}
