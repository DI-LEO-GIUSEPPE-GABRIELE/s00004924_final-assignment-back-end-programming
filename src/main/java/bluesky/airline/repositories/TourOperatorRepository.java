package bluesky.airline.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bluesky.airline.entities.TourOperator;

// Repository for TourOperator entities
@Repository
public interface TourOperatorRepository extends JpaRepository<TourOperator, UUID> {
}
