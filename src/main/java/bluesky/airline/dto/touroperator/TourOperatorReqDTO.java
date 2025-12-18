package bluesky.airline.dto.touroperator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class TourOperatorReqDTO {
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    @NotBlank(message = "VAT number is required")
    private String vatNumber;
    
    @NotNull(message = "User ID is required")
    private UUID userId;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
