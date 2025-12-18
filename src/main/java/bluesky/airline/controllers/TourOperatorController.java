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
import bluesky.airline.services.UserService;
import bluesky.airline.dto.touroperator.TourOperatorReqDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/operators")
public class TourOperatorController {
    @Autowired
    private TourOperatorService operators;
    @Autowired
    private UserService users;

    @GetMapping
    public Page<TourOperator> list(Pageable pageable) {
        return operators.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourOperator> get(@PathVariable UUID id) {
        TourOperator op = operators.findById(id);
        if (op == null)
            throw new bluesky.airline.exceptions.NotFoundException("Tour Operator not found: " + id);
        return ResponseEntity.ok(op);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TourOperator> create(@RequestBody @Valid TourOperatorReqDTO body) {
        TourOperator op = new TourOperator();
        updateOperatorFromDTO(op, body);
        op = operators.save(op);
        return ResponseEntity.created(java.net.URI.create("/operators/" + op.getId())).body(op);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TourOperator> update(@PathVariable UUID id, @RequestBody @Valid TourOperatorReqDTO body) {
        TourOperator found = operators.findById(id);
        if (found == null)
            throw new bluesky.airline.exceptions.NotFoundException("Tour Operator not found: " + id);
        updateOperatorFromDTO(found, body);
        return ResponseEntity.ok(operators.save(found));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!operators.existsById(id))
            throw new bluesky.airline.exceptions.NotFoundException("Tour Operator not found: " + id);
        operators.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void updateOperatorFromDTO(TourOperator op, TourOperatorReqDTO body) {
        op.setCompanyName(body.getCompanyName());
        op.setVatNumber(body.getVatNumber());
        if (body.getUserId() != null) {
            bluesky.airline.entities.User u = users.findById(body.getUserId())
                    .orElseThrow(() -> new bluesky.airline.exceptions.NotFoundException(
                            "User not found: " + body.getUserId()));
            op.setUser(u);
        }
    }
}
