package study_project.demo.controllers;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import study_project.demo.services.ValidationException;

/**
 * Gestione centralizzata degli errori di validazione.
 */
@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> onValidation(ValidationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", "VALIDATION_ERROR");
        body.put("message", ex.getMessage());
        body.put("field", ex.getField());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}