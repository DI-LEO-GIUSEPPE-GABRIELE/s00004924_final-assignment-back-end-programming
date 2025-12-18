package bluesky.airline.controllers;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.TourOperator;

@RestController
@RequestMapping("/operators")
public class TourOperatorController {
    @org.springframework.beans.factory.annotation.Autowired
    private bluesky.airline.services.TourOperatorService operators;

    @GetMapping
    public Page<TourOperator> list(Pageable pageable) {
        return operators.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourOperator> get(@PathVariable UUID id) {
        TourOperator op = operators.findById(id);
        if (op == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(op);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TourOperator> create(@RequestBody TourOperator body) {
        TourOperator op = operators.save(body);
        return ResponseEntity.created(java.net.URI.create("/operators/" + op.getId())).body(op);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TourOperator> update(@PathVariable UUID id, @RequestBody TourOperator body) {
        TourOperator found = operators.findById(id);
        if (found == null)
            return ResponseEntity.notFound().build();
        body.setId(found.getId());
        return ResponseEntity.ok(operators.save(body));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!operators.existsById(id))
            return ResponseEntity.notFound().build();
        operators.delete(id);
        return ResponseEntity.noContent().build();
    }
}
