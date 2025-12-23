package bluesky.airline.services;

import bluesky.airline.dto.reservation.ReservationReqDTO;
import bluesky.airline.dto.reservation.ReservationRespDTO;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.Flight;
import bluesky.airline.repositories.UserRepository;
import bluesky.airline.repositories.ReservationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import bluesky.airline.entities.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import bluesky.airline.entities.User;
import org.springframework.data.domain.Page;
import bluesky.airline.entities.enums.ReservationStatus;
import bluesky.airline.entities.enums.RoleType;
import bluesky.airline.exceptions.NotFoundException;
import bluesky.airline.exceptions.ValidationException;
import org.springframework.data.domain.Pageable;
import java.time.Instant;

// Service for Reservation entities
@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservations;
    @Autowired
    private FlightService flights;
    @Autowired
    private UserRepository users;

    // Find all reservations with pagination
    public Page<Reservation> findAll(Pageable pageable) {
        return reservations.findAll(pageable);
    }

    // Create a new reservation
    public Reservation create(ReservationReqDTO body) {
        Reservation r = new Reservation();
        updateReservationFromDTO(r, body);

        if (r.getReservationDate() == null)
            r.setReservationDate(Instant.now());
        if (r.getStatus() == null)
            r.setStatus(ReservationStatus.PENDING);

        return reservations.save(r);
    }

    // Update a reservation from a ReservationReqDTO
    private void updateReservationFromDTO(Reservation r, ReservationReqDTO body) {
        List<Flight> flightList = new ArrayList<>();
        if (body.getFlightIds() != null) {
            for (UUID flightId : body.getFlightIds()) {
                if (flightId == null)
                    continue;
                Flight f = flights.findById(flightId);
                if (f == null)
                    throw new NotFoundException("Flight not found: " + flightId);
                flightList.add(f);
            }
        }
        r.setFlights(flightList);

        User u = users.findWithRolesById(body.getUserId()).orElse(null);
        if (u == null)
            throw new NotFoundException(
                    "User not found: " + body.getUserId());

        // Verify that user is a Tour Operator
        boolean isTourOperator = false;
        if (u.getRoles() != null) {
            isTourOperator = u.getRoles().stream()
                    .anyMatch(role -> role != null && role.getName() != null && role.getName()
                            .equalsIgnoreCase(RoleType.TOUR_OPERATOR.name()));
        }

        if (!isTourOperator) {
            throw new ValidationException(
                    List.of("userId: User is not a Tour Operator"));
        }
        r.setUser(u);

        if (body.getStatus() != null)
            r.setStatus(body.getStatus());
    }

    // Find all reservations with a specific status and pagination
    public Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable) {
        return reservations.findByStatus(status, pageable);
    }

    // Find a reservation by its ID
    public Reservation findById(UUID id) {
        return reservations.findById(id).orElse(null);
    }

    // Save a reservation (create or update)
    public Reservation save(Reservation reservation) {
        return reservations.save(reservation);
    }

    // Delete a reservation by its ID
    public void delete(UUID id) {
        reservations.deleteById(id);
    }

    // Check if a reservation exists by its ID
    public boolean existsById(UUID id) {
        return reservations.existsById(id);
    }

    // Convert a Reservation entity to a ReservationRespDTO
    public ReservationRespDTO toDTO(Reservation r) {
        ReservationRespDTO dto = new ReservationRespDTO();
        dto.setId(r.getId());
        dto.setReservationDate(r.getReservationDate());
        dto.setStatus(r.getStatus());

        if (r.getUser() != null) {
            ReservationRespDTO.UserSummaryDTO u = new ReservationRespDTO.UserSummaryDTO();
            u.setId(r.getUser().getId());
            u.setName(r.getUser().getName());
            u.setSurname(r.getUser().getSurname());
            u.setEmail(r.getUser().getEmail());
            dto.setUser(u);
        }

        if (r.getFlights() != null) {
            dto.setFlights(r.getFlights().stream().map(flights::toDTO).toList());
        }

        return dto;
    }
}
