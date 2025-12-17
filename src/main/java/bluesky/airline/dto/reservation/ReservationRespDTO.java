package bluesky.airline.dto.reservation;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.Instant;
import bluesky.airline.entities.enums.ReservationStatus;
import bluesky.airline.dto.flight.FlightRespDTO;
import bluesky.airline.dto.touroperator.TourOperatorRespDTO;

public class ReservationRespDTO {
    private UUID id;
    private Instant reservationDate;
    private ReservationStatus status;
    private BigDecimal totalPrice;
    private FlightRespDTO flight;
    private TourOperatorRespDTO tourOperator;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public FlightRespDTO getFlight() {
        return flight;
    }

    public void setFlight(FlightRespDTO flight) {
        this.flight = flight;
    }

    public TourOperatorRespDTO getTourOperator() {
        return tourOperator;
    }

    public void setTourOperator(TourOperatorRespDTO tourOperator) {
        this.tourOperator = tourOperator;
    }
}
