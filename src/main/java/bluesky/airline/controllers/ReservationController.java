package bluesky.airline.controllers;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import bluesky.airline.entities.Reservation;
import bluesky.airline.entities.enums.ReservationStatus;
import bluesky.airline.dto.reservation.ReservationReqDTO;
import bluesky.airline.dto.reservation.ReservationRespDTO;
import bluesky.airline.dto.flight.FlightRespDTO;
import bluesky.airline.dto.airport.AirportRespDTO;
import bluesky.airline.dto.aircraft.AircraftRespDTO;
import bluesky.airline.entities.Flight;
import bluesky.airline.entities.Airport;
import bluesky.airline.entities.Aircraft;
import bluesky.airline.entities.PassengerAircraft;
import bluesky.airline.entities.CargoAircraft;

// Controller for reservation management, accessible by ADMIN and TOUR_OPERATOR roles
// Endpoint: /reservations
@RestController
@RequestMapping("/reservations")
@PreAuthorize("hasRole('ADMIN') or hasRole('TOUR_OPERATOR')")
public class ReservationController {
    @org.springframework.beans.factory.annotation.Autowired
    private bluesky.airline.services.ReservationService reservations;

    // List reservations endpoint
    // Endpoint: GET /reservations
    @GetMapping
    public Page<ReservationRespDTO> list(@RequestParam(required = false) ReservationStatus status, Pageable pageable) {
        Page<Reservation> page;
        if (status != null)
            page = reservations.findByStatus(status, pageable);
        else
            page = reservations.findAll(pageable);
        return page.map(this::toDTO);
    }

    // Get reservation details endpoint
    // Endpoint: GET /reservations/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ReservationRespDTO> get(@PathVariable UUID id) {
        Reservation r = reservations.findById(id);
        if (r == null)
            throw new bluesky.airline.exceptions.NotFoundException("Reservation not found: " + id);
        return ResponseEntity.ok(toDTO(r));
    }

    // Create reservation endpoint
    // Endpoint: POST /reservations
    @PostMapping
    public ResponseEntity<ReservationRespDTO> create(@RequestBody @Valid ReservationReqDTO body) {
        Reservation r = reservations.create(body);
        return ResponseEntity.created(java.net.URI.create("/reservations/" + r.getId())).body(toDTO(r));
    }

    // Update reservation status endpoint, only accessible by ADMIN and
    // TOUR_OPERATOR roles
    // Endpoint: PUT /reservations/{id}/status
    @PreAuthorize("hasRole('ADMIN') or hasRole('TOUR_OPERATOR')")
    @PutMapping("/{id}/status")
    public ResponseEntity<ReservationRespDTO> updateStatus(@PathVariable UUID id,
            @RequestParam ReservationStatus status) {
        Reservation r = reservations.findById(id);
        if (r == null)
            throw new bluesky.airline.exceptions.NotFoundException("Reservation not found: " + id);
        r.setStatus(status);
        return ResponseEntity.ok(toDTO(reservations.save(r)));
    }

    // Delete reservation endpoint, only accessible by ADMIN role
    // Endpoint: DELETE /reservations/{id}
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!reservations.existsById(id))
            throw new bluesky.airline.exceptions.NotFoundException("Reservation not found: " + id);
        reservations.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ReservationRespDTO toDTO(Reservation r) {
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
            dto.setFlights(r.getFlights().stream().map(this::toDTO).toList());
        }

        return dto;
    }

    private FlightRespDTO toDTO(Flight f) {
        FlightRespDTO dto = new FlightRespDTO();
        dto.setId(f.getId());
        dto.setFlightCode(f.getFlightCode());
        dto.setDepartureDate(f.getDepartureDate());
        dto.setArrivalDate(f.getArrivalDate());
        dto.setBasePrice(f.getBasePrice());
        dto.setStatus(f.getStatus());
        if (f.getDepartureAirport() != null) {
            dto.setDepartureAirport(toDTO(f.getDepartureAirport()));
        }
        if (f.getArrivalAirport() != null) {
            dto.setArrivalAirport(toDTO(f.getArrivalAirport()));
        }
        if (f.getAircraft() != null) {
            dto.setAircraft(toDTO(f.getAircraft()));
        }
        if (f.getCompartments() != null) {
            dto.setCompartmentCodes(f.getCompartments().stream()
                    .map(c -> c.getCompartmentCode())
                    .toList());
        }
        return dto;
    }

    private AirportRespDTO toDTO(Airport a) {
        AirportRespDTO dto = new AirportRespDTO();
        dto.setId(a.getId());
        dto.setCode(a.getCode());
        dto.setName(a.getName());
        dto.setCity(a.getCity());
        dto.setCountry(a.getCountry());
        return dto;
    }

    private AircraftRespDTO toDTO(Aircraft a) {
        AircraftRespDTO dto = new AircraftRespDTO();
        dto.setId(a.getId());
        dto.setBrand(a.getBrand());
        dto.setModel(a.getModel());

        if (a instanceof PassengerAircraft) {
            dto.setType("PASSENGER");
            dto.setTotalSeats(((PassengerAircraft) a).getTotalSeats());
        } else if (a instanceof CargoAircraft) {
            dto.setType("CARGO");
            dto.setMaxLoadCapacity(((CargoAircraft) a).getMaxLoadCapacity());
        }

        return dto;
    }
}
