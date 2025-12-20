package bluesky.airline.dto.compartment;

import java.util.UUID;
import bluesky.airline.entities.enums.CompartmentCode;

// DTO for Compartment responses
public class CompartmentRespDTO {
    private UUID id;
    private CompartmentCode compartmentCode;
    private UUID flightId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CompartmentCode getCompartmentCode() {
        return compartmentCode;
    }

    public void setCompartmentCode(CompartmentCode compartmentCode) {
        this.compartmentCode = compartmentCode;
    }

    public UUID getFlightId() {
        return flightId;
    }

    public void setFlightId(UUID flightId) {
        this.flightId = flightId;
    }
}
