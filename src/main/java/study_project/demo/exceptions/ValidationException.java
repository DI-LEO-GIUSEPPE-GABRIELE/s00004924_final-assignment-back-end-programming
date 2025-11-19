package study_project.demo.exceptions;

/**
 * Eccezione di validazione: segnala input non valido con contesto del campo.
 */
public class ValidationException extends RuntimeException {
    private final String field;
    public ValidationException(String field, String message) { super(message); this.field = field; }
    public String getField() { return field; }
}