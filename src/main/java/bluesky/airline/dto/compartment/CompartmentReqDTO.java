package bluesky.airline.dto.compartment;

import jakarta.validation.constraints.NotBlank;

// DTO for Compartment requests (create/update)
public class CompartmentReqDTO {
    @NotBlank(message = "Compartment Code is required")
    private String compartmentCode;

    private String description;

    public String getCompartmentCode() {
        return compartmentCode;
    }

    public void setCompartmentCode(String compartmentCode) {
        this.compartmentCode = compartmentCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
