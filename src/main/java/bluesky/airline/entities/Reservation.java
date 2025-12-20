package bluesky.airline.entities;

import java.math.BigDecimal;
import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import bluesky.airline.entities.enums.ReservationStatus;

// Entity for Reservations
@Entity
@Table(name = "reservations")
public class Reservation extends BaseUuidEntity {
    @Column(name = "reservation_date", nullable = false)
    private Instant reservationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ReservationStatus status;

    // Many-to-One: each reservation belongs to one user (Tour Operator role)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Many-to-Many: each reservation can have multiple flights
    @jakarta.persistence.ManyToMany(fetch = FetchType.LAZY)
    @jakarta.persistence.JoinTable(name = "reservations_flights", joinColumns = @JoinColumn(name = "reservation_id"), inverseJoinColumns = @JoinColumn(name = "flight_id"))
    private java.util.List<Flight> flights;

    public Instant getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Instant reservationDate) {
        this.reservationDate = reservationDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public java.util.List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(java.util.List<Flight> flights) {
        this.flights = flights;
    }
}
