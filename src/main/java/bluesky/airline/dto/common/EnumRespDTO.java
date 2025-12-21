package bluesky.airline.dto.common;

// DTO for Enum responses
public class EnumRespDTO {
    private String label;
    private String value;

    public EnumRespDTO(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
