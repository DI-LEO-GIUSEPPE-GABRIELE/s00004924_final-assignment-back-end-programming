package bluesky.airline.services;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.TourOperator;
import bluesky.airline.repositories.TourOperatorRepository;

@Service
public class TourOperatorService {
    @Autowired
    private TourOperatorRepository operators;

    public Page<TourOperator> findAll(Pageable pageable) {
        return operators.findAll(pageable);
    }

    public TourOperator findById(UUID id) {
        return operators.findById(id).orElse(null);
    }

    public TourOperator save(TourOperator operator) {
        return operators.save(operator);
    }

    public void delete(UUID id) {
        operators.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return operators.existsById(id);
    }
}
