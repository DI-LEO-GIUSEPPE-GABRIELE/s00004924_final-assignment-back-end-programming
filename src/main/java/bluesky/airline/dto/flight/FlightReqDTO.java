package bluesky.airline.dto.flight;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Future;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import bluesky.airline.entities.enums.FlightStatus;

import java.util.List;

// DTO for Flight requests (create/update)
public class FlightReqDTO {
    @NotBlank(message = "Flight code is required")
    private String flightCode;

    @NotNull(message = "Departure date is required")
    @Future(message = "Departure date must be in the future")
    private Instant departureDate;

    @NotNull(message = "Arrival date is required")
    @Future(message = "Arrival date must be in the future")
    private Instant arrivalDate;

    @NotNull(message = "Base price is required")
    private BigDecimal basePrice;

    @NotNull(message = "Status is required")
    private FlightStatus status;

    @NotNull(message = "Departure airport ID is required")
    private UUID departureAirportId;

    @NotNull(message = "Arrival airport ID is required")
    private UUID arrivalAirportId;

    @NotNull(message = "Aircraft ID is required")
    private UUID aircraftId;

    private List<String> compartmentCodes;

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

    public UUID getDepartureAirportId() {
        return departureAirportId;
    }

    public void setDepartureAirportId(UUID departureAirportId) {
        this.departureAirportId = departureAirportId;
    }

    public UUID getArrivalAirportId() {
        return arrivalAirportId;
    }

    public void setArrivalAirportId(UUID arrivalAirportId) {
        this.arrivalAirportId = arrivalAirportId;
    }

    public UUID getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(UUID aircraftId) {
        this.aircraftId = aircraftId;
    }

    public List<String> getCompartmentCodes() {
        return compartmentCodes;
    }

    public void setCompartmentCodes(List<String> compartmentCodes) {
        this.compartmentCodes = compartmentCodes;
    }
}
