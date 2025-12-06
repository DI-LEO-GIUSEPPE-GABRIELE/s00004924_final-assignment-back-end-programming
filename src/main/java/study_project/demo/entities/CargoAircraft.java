package study_project.demo.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;

@Entity
@DiscriminatorValue("CARGO")
public class CargoAircraft extends Aircraft {
    @Column(name = "max_load_capacity")
    private Integer maxLoadCapacity;

    public Integer getMaxLoadCapacity() { return maxLoadCapacity; }
    public void setMaxLoadCapacity(Integer maxLoadCapacity) { this.maxLoadCapacity = maxLoadCapacity; }
}
