package bluesky.airline.controllers;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.TourOperator;
import bluesky.airline.services.TourOperatorService;
import bluesky.airline.dto.touroperator.TourOperatorReqDTO;
import jakarta.validation.Valid;

// Controller for tour operators, only accessible by ADMIN role
// Endpoint: /operators
@RestController
@RequestMapping("/operators")
@PreAuthorize("hasRole('ADMIN')")
public class TourOperatorController {
    @Autowired
    private TourOperatorService operators;

    // List tour operators endpoint
    // Endpoint: GET /operators
    @GetMapping
    public Page<TourOperator> list(Pageable pageable) {
        return operators.findAll(pageable);
    }

    // Get tour operator by ID endpoint
    // Endpoint: GET /operators/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TourOperator> get(@PathVariable UUID id) {
        TourOperator op = operators.findById(id);
        if (op == null)
            throw new bluesky.airline.exceptions.NotFoundException("Tour Operator not found: " + id);
        return ResponseEntity.ok(op);
    }

    // Create tour operator endpoint
    // Endpoint: POST /operators
    @PostMapping
    public ResponseEntity<TourOperator> create(@RequestBody @Valid TourOperatorReqDTO body) {
        TourOperator op = operators.create(body);
        return ResponseEntity.created(java.net.URI.create("/operators/" + op.getId())).body(op);
    }

    // Update tour operator endpoint
    // Endpoint: PUT /operators/{id}
    @PutMapping("/{id}")
    public ResponseEntity<TourOperator> update(@PathVariable UUID id, @RequestBody @Valid TourOperatorReqDTO body) {
        return ResponseEntity.ok(operators.update(id, body));
    }

    // Delete tour operator endpoint
    // Endpoint: DELETE /operators/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!operators.existsById(id))
            throw new bluesky.airline.exceptions.NotFoundException("Tour Operator not found: " + id);
        operators.delete(id);
        return ResponseEntity.noContent().build();
    }
}
