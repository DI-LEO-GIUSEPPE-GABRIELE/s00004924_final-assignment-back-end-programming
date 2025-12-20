package bluesky.airline.services;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.Price;
import bluesky.airline.entities.Flight;
import bluesky.airline.repositories.PriceRepository;
import bluesky.airline.repositories.FlightRepository;
import bluesky.airline.dto.price.PriceReqDTO;
import bluesky.airline.exceptions.NotFoundException;

@Service
public class PriceService {
    @Autowired
    private PriceRepository prices;
    @Autowired
    private FlightRepository flights;

    public Page<Price> findAll(Pageable pageable) {
        return prices.findAll(pageable);
    }

    public Page<Price> findByFlightId(UUID flightId, Pageable pageable) {
        return prices.findByFlightId(flightId, pageable);
    }

    public Price findById(UUID id) {
        return prices.findById(id).orElseThrow(() -> new NotFoundException("Price not found: " + id));
    }

    public Price create(PriceReqDTO body) {
        Price p = new Price();
        updatePriceFromDTO(p, body);
        return prices.save(p);
    }

    public Price update(UUID id, PriceReqDTO body) {
        Price p = findById(id);
        updatePriceFromDTO(p, body);
        return prices.save(p);
    }

    public void delete(UUID id) {
        if (!prices.existsById(id)) {
            throw new NotFoundException("Price not found: " + id);
        }
        prices.deleteById(id);
    }

    private void updatePriceFromDTO(Price p, PriceReqDTO body) {
        p.setPriceCode(body.getPriceCode());
        p.setBasePrice(body.getBasePrice());
        
        Flight f = flights.findById(body.getFlightId()).orElseThrow(() -> 
            new NotFoundException("Flight not found: " + body.getFlightId()));
        p.setFlight(f);
    }
}
