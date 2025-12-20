package bluesky.airline.dto.price;

import java.math.BigDecimal;
import java.util.UUID;
import bluesky.airline.entities.enums.PriceCode;

// DTO for Price responses

public class PriceRespDTO {
    private UUID id;
    private PriceCode priceCode;
    private BigDecimal basePrice;
    private UUID flightId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PriceCode getPriceCode() {
        return priceCode;
    }

    public void setPriceCode(PriceCode priceCode) {
        this.priceCode = priceCode;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public UUID getFlightId() {
        return flightId;
    }

    public void setFlightId(UUID flightId) {
        this.flightId = flightId;
    }
}
