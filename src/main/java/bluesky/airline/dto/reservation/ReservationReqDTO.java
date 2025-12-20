package bluesky.airline.dto.reservation;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import java.util.List;
import bluesky.airline.entities.enums.ReservationStatus;

// DTO for Reservation requests (create/update)
public class ReservationReqDTO {
    @NotNull(message = "Flight IDs are required")
    private List<UUID> flightIds;

    @NotNull(message = "User ID (Tour Operator) is required")
    private UUID userId;

    private ReservationStatus status;

    public List<UUID> getFlightIds() {
        return flightIds;
    }

    public void setFlightIds(List<UUID> flightIds) {
        this.flightIds = flightIds;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
