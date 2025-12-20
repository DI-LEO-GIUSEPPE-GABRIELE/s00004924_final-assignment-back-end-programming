package bluesky.airline.dto.compartment;

import java.util.UUID;

// DTO for Compartment responses
public class CompartmentRespDTO {
    private UUID id;
    private String compartmentCode;
    private String description;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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
