package bluesky.airline.dto.airport;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AirportReqDTO {
    @NotBlank(message = "Code is required")
    @Size(max = 10, message = "Code must be max 10 characters")
    private String code;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "Country is required")
    private String country;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
