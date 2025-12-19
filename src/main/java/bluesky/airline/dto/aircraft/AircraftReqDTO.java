package bluesky.airline.dto.aircraft;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// DTO for Aircraft requests (create/update)
public class AircraftReqDTO {
    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Type is required (PASSENGER/CARGO)")
    private String type;

    @Min(value = 0, message = "Total seats must be positive")
    private Integer totalSeats;

    @Min(value = 0, message = "Max load capacity must be positive")
    private Integer maxLoadCapacity;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getMaxLoadCapacity() {
        return maxLoadCapacity;
    }

    public void setMaxLoadCapacity(Integer maxLoadCapacity) {
        this.maxLoadCapacity = maxLoadCapacity;
    }
}
