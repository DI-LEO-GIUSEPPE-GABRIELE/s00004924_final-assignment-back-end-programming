package bluesky.airline.dto.reservation;

import java.util.List;
import java.time.Instant;
import java.util.UUID;
import bluesky.airline.entities.enums.ReservationStatus;
import bluesky.airline.dto.flight.FlightRespDTO;

// DTO for Reservation responses
public class ReservationRespDTO {
    private UUID id;
    private Instant reservationDate;
    private ReservationStatus status;
    private UserSummaryDTO user;
    private List<FlightRespDTO> flights;

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

    public UserSummaryDTO getUser() {
        return user;
    }

    public void setUser(UserSummaryDTO user) {
        this.user = user;
    }

    public List<FlightRespDTO> getFlights() {
        return flights;
    }

    public void setFlights(List<FlightRespDTO> flights) {
        this.flights = flights;
    }

    public static class UserSummaryDTO {
        private UUID id;
        private String name;
        private String surname;
        private String email;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
