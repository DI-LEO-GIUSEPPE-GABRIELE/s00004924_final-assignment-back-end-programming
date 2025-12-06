package bluesky.airline.exceptions;

/**
 * Validation exception: signals invalid input and the related field context.
 */
public class ValidationException extends RuntimeException {
    private final String field;

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
