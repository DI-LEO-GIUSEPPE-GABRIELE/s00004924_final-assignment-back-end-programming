package bluesky.airline.entities;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import bluesky.airline.entities.enums.PriceCode;

// Entity for Prices
@Entity
@Table(name = "prices")
public class Price extends BaseUuidEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "price_code", nullable = false)
    private PriceCode priceCode;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    // Many-to-One: each price belongs to one flight
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

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

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }
}
