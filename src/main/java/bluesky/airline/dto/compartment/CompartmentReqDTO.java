package bluesky.airline.dto.compartment;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import bluesky.airline.entities.enums.CompartmentCode;

// DTO for Compartment requests (create/update)
public class CompartmentReqDTO {
    @NotNull(message = "Compartment code is required")
    private CompartmentCode compartmentCode;

    @NotNull(message = "Flight ID is required")
    private UUID flightId;

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
