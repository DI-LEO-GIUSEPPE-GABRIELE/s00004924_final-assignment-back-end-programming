package bluesky.airline.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;

// Entity for Passenger Aircrafts
@Entity
@DiscriminatorValue("PASSENGER")
public class PassengerAircraft extends Aircraft {
    @Column(name = "total_seats")
    private Integer totalSeats;

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }
}
