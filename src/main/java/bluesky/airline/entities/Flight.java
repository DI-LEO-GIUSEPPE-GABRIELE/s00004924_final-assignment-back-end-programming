package bluesky.airline.entities;

import bluesky.airline.entities.enums.FlightStatus;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinTable;
import java.util.HashSet;
import java.util.Set;
import java.time.Instant;
import java.math.BigDecimal;

// Entity for Flights
@Entity
@Table(name = "flights")
public class Flight extends BaseUuidEntity {
    @Column(name = "flight_code", nullable = false, length = 50, unique = true)
    private String flightCode;

    @Column(name = "departure_date", nullable = false)
    private Instant departureDate;

    @Column(name = "arrival_date", nullable = false)
    private Instant arrivalDate;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private FlightStatus status;

    // Many-to-One: each flight has one departure airport
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_airport_id")
    private Airport departureAirport;

    // Many-to-One: each flight has one arrival airport
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_airport_id")
    private Airport arrivalAirport;

    // Many-to-One: each flight has one aircraft
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;

    // One-to-One: each flight has one weather data
    @OneToOne(mappedBy = "flight", fetch = FetchType.LAZY)
    private WeatherData weatherData;

    // Many-to-Many: each flight can have many compartments and each compartment can
    // have many flights
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "flights_compartments", joinColumns = @JoinColumn(name = "flight_id"), inverseJoinColumns = @JoinColumn(name = "compartment_id"))
    private Set<Compartment> compartments = new HashSet<>();

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    public Instant getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Instant departureDate) {
        this.departureDate = departureDate;
    }

    public Instant getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Instant arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public Airport getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(Airport departureAirport) {
        this.departureAirport = departureAirport;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public WeatherData getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(WeatherData weatherData) {
        this.weatherData = weatherData;
    }

    public Set<Compartment> getCompartments() {
        return compartments;
    }

    public void setCompartments(Set<Compartment> compartments) {
        this.compartments = compartments;
    }
}
