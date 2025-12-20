package bluesky.airline.controllers;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.Price;
import bluesky.airline.services.PriceService;
import bluesky.airline.dto.price.PriceReqDTO;
import bluesky.airline.dto.price.PriceRespDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/prices")
@PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
public class PriceController {
    @Autowired
    private PriceService prices;

    @GetMapping
    @PreAuthorize("permitAll()")
    public Page<PriceRespDTO> list(
            @RequestParam(required = false) UUID flightId,
            Pageable pageable) {
        Page<Price> page;
        if (flightId != null) {
            page = prices.findByFlightId(flightId, pageable);
        } else {
            page = prices.findAll(pageable);
        }
        return page.map(this::toDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PriceRespDTO> get(@PathVariable UUID id) {
        Price p = prices.findById(id);
        return ResponseEntity.ok(toDTO(p));
    }

    @PostMapping
    public ResponseEntity<PriceRespDTO> create(@RequestBody @Valid PriceReqDTO body) {
        Price p = prices.create(body);
        return ResponseEntity.created(java.net.URI.create("/prices/" + p.getId())).body(toDTO(p));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PriceRespDTO> update(@PathVariable UUID id, @RequestBody @Valid PriceReqDTO body) {
        return ResponseEntity.ok(toDTO(prices.update(id, body)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        prices.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/codes")
    @PreAuthorize("permitAll()")
    public ResponseEntity<java.util.List<bluesky.airline.dto.common.EnumRespDTO>> getCodes() {
        return ResponseEntity.ok(java.util.Arrays.stream(bluesky.airline.entities.enums.PriceCode.values())
                .map(s -> new bluesky.airline.dto.common.EnumRespDTO(s.name(), s.name()))
                .toList());
    }

    private PriceRespDTO toDTO(Price p) {
        PriceRespDTO dto = new PriceRespDTO();
        dto.setId(p.getId());
        dto.setPriceCode(p.getPriceCode());
        dto.setBasePrice(p.getBasePrice());
        if (p.getFlight() != null) {
            dto.setFlightId(p.getFlight().getId());
        }
        return dto;
    }
}
