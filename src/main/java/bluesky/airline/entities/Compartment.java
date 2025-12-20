package bluesky.airline.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import bluesky.airline.entities.enums.CompartmentCode;

// Entity for Compartments
@Entity
@Table(name = "compartments")
public class Compartment extends BaseUuidEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "compartment_code", nullable = false)
    private CompartmentCode compartmentCode;

    // Many-to-One: each compartment belongs to one flight
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    public CompartmentCode getCompartmentCode() {
        return compartmentCode;
    }

    public void setCompartmentCode(CompartmentCode compartmentCode) {
        this.compartmentCode = compartmentCode;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }
}
