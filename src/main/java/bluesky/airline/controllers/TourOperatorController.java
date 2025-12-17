package bluesky.airline.controllers;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.TourOperator;
import bluesky.airline.repositories.TourOperatorRepository;

@RestController
@RequestMapping("/operators")
public class TourOperatorController {
    @org.springframework.beans.factory.annotation.Autowired
    private TourOperatorRepository operators;

    @GetMapping
    public Page<TourOperator> list(Pageable pageable) {
        return operators.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourOperator> get(@PathVariable UUID id) {
        return operators.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
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
        return operators.findById(id).map(found -> {
            body.setId(found.getId());
            return ResponseEntity.ok(operators.save(body));
        }).orElse(ResponseEntity.notFound().build());
    }
}
