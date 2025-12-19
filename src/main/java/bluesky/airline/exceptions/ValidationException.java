package bluesky.airline.exceptions;

import java.util.List;

// Exception for validation errors
public class ValidationException extends RuntimeException {
    private List<String> errorsList;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(List<String> errorsList) {
        super("Errori nel payload");
        this.errorsList = errorsList;
    }

    public List<String> getErrorsList() {
        return errorsList;
    }
}
