package bluesky.airline.dto.price;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

import bluesky.airline.entities.enums.PriceCode;

// DTO for Price requests (create/update)
public class PriceReqDTO {
    @NotNull(message = "Price code is required")
    private PriceCode priceCode;

    @NotNull(message = "Base price is required")
    private BigDecimal basePrice;

    @NotNull(message = "Flight ID is required")
    private UUID flightId;

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
