package bluesky.airline.controllers;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import bluesky.airline.entities.Compartment;
import bluesky.airline.services.CompartmentService;
import bluesky.airline.dto.compartment.CompartmentReqDTO;
import bluesky.airline.dto.compartment.CompartmentRespDTO;
import bluesky.airline.entities.enums.CompartmentCode;
import bluesky.airline.exceptions.NotFoundException;

// Controller for Compartments
@RestController
@RequestMapping("/compartments")
public class CompartmentController {
    @Autowired
    private CompartmentService compartments;

    // List all compartments with pagination, optional filter by flightId
    // Endpoint: GET /compartments
    @GetMapping
    @PreAuthorize("permitAll()")
    public Page<CompartmentRespDTO> list(
            @RequestParam(required = false) UUID flightId,
            Pageable pageable) {
        Page<Compartment> page;
        if (flightId != null) {
            page = compartments.findByFlightId(flightId, pageable);
        } else {
            page = compartments.findAll(pageable);
        }
        return page.map(this::toDTO);
    }

    // Get details of a specific compartment by ID
    // Endpoint: GET /compartments/{id}
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CompartmentRespDTO> get(@PathVariable UUID id) {
        Compartment c = compartments.findById(id);
        if (c == null)
            throw new NotFoundException("Compartment not found: " + id);
        return ResponseEntity.ok(toDTO(c));
    }

    // Get all compartment codes
    // Endpoint: GET /compartments/codes
    @GetMapping("/codes")
    @PreAuthorize("permitAll()")
    public ResponseEntity<java.util.List<bluesky.airline.dto.common.EnumRespDTO>> getCodes() {
        return ResponseEntity.ok(java.util.Arrays.stream(CompartmentCode.values())
                .map(c -> new bluesky.airline.dto.common.EnumRespDTO(c.name(), c.name()))
                .toList());
    }

    // Create a new compartment, only accessible by ADMIN
    // Endpoint: POST /compartments
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompartmentRespDTO> create(@RequestBody @jakarta.validation.Valid CompartmentReqDTO body) {
        Compartment c = compartments.create(body);
        return ResponseEntity.status(201).body(toDTO(c));
    }

    // Update an existing compartment, only accessible by ADMIN
    // Endpoint: PUT /compartments/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompartmentRespDTO> update(@PathVariable UUID id,
            @RequestBody @jakarta.validation.Valid CompartmentReqDTO body) {
        Compartment c = compartments.update(id, body);
        return ResponseEntity.ok(toDTO(c));
    }

    // Delete a compartment, only accessible by ADMIN
    // Endpoint: DELETE /compartments/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!compartments.existsById(id))
            throw new NotFoundException("Compartment not found: " + id);
        compartments.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CompartmentRespDTO toDTO(Compartment c) {
        CompartmentRespDTO dto = new CompartmentRespDTO();
        dto.setId(c.getId());
        dto.setCompartmentCode(c.getCompartmentCode());
        if (c.getFlight() != null) {
            dto.setFlightId(c.getFlight().getId());
        }
        return dto;
    }
}
