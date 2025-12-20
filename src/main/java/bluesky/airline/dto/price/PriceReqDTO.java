package bluesky.airline.dto.price;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.UUID;

public class PriceReqDTO {
    @NotBlank(message = "Price code is required")
    private String priceCode;

    @NotNull(message = "Base price is required")
    private BigDecimal basePrice;

    @NotNull(message = "Flight ID is required")
    private UUID flightId;

    public String getPriceCode() {
        return priceCode;
    }

    public void setPriceCode(String priceCode) {
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
