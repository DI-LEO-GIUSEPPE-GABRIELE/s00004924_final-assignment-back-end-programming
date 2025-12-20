package bluesky.airline.repositories;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import bluesky.airline.entities.Price;

public interface PriceRepository extends JpaRepository<Price, UUID> {
    Optional<Price> findByPriceCode(String priceCode);
}
