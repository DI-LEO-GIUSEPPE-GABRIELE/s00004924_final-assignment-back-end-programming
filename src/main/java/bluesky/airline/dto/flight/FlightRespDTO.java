package bluesky.airline.dto.flight;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import bluesky.airline.entities.enums.FlightStatus;
import bluesky.airline.dto.airport.AirportRespDTO;
import bluesky.airline.dto.aircraft.AircraftRespDTO;
import bluesky.airline.entities.enums.CompartmentCode;
import java.util.List;

// DTO for Flight responses
public class FlightRespDTO {
    private UUID id;
    private String flightCode;
    private Instant departureDate;
    private Instant arrivalDate;
    private BigDecimal basePrice;
    private FlightStatus status;
    private AirportRespDTO departureAirport;
    private AirportRespDTO arrivalAirport;
    private AircraftRespDTO aircraft;
    private List<CompartmentCode> compartmentCodes;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public AirportRespDTO getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(AirportRespDTO departureAirport) {
        this.departureAirport = departureAirport;
    }

    public AirportRespDTO getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(AirportRespDTO arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public AircraftRespDTO getAircraft() {
        return aircraft;
    }

    public void setAircraft(AircraftRespDTO aircraft) {
        this.aircraft = aircraft;
    }

    public List<CompartmentCode> getCompartmentCodes() {
        return compartmentCodes;
    }

    public void setCompartmentCodes(List<CompartmentCode> compartmentCodes) {
        this.compartmentCodes = compartmentCodes;
    }
}
