package bluesky.airline.dto.reservation;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import java.math.BigDecimal;
import bluesky.airline.entities.enums.ReservationStatus;

public class ReservationReqDTO {
    @NotNull(message = "Flight ID is required")
    private UUID flightId;
    
    @NotNull(message = "Tour Operator ID is required")
    private UUID tourOperatorId;
    
    @NotNull(message = "Total price is required")
    private BigDecimal totalPrice;
    
    private ReservationStatus status;

    public UUID getFlightId() {
        return flightId;
    }

    public void setFlightId(UUID flightId) {
        this.flightId = flightId;
    }

    public UUID getTourOperatorId() {
        return tourOperatorId;
    }

    public void setTourOperatorId(UUID tourOperatorId) {
        this.tourOperatorId = tourOperatorId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
