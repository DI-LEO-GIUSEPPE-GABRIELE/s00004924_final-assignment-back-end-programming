package bluesky.airline.entities.enums;

// Enum for User roles
public enum RoleType {
    ADMIN(0),
    FLIGHT_MANAGER(1),
    TOUR_OPERATOR(2);

    private final int code;

    RoleType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
