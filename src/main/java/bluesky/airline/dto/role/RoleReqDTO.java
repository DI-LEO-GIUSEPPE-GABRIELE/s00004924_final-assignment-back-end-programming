package bluesky.airline.dto.role;

import jakarta.validation.constraints.NotBlank;

public class RoleReqDTO {
    @NotBlank(message = "Role name is required")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
