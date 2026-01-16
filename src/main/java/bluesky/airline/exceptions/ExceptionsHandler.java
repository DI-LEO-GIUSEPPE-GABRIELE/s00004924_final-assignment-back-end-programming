package bluesky.airline.exceptions;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import bluesky.airline.dto.errors.ErrorWithListDTO;
import bluesky.airline.dto.errors.ErrorDTO;

// Handler for exceptions
@RestControllerAdvice
public class ExceptionsHandler {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionsHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorWithListDTO handleValidationErrors(MethodArgumentNotValidException ex) {
		List<String> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.toList();
		return new ErrorWithListDTO("Validation errors", LocalDateTime.now(), errors);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorWithListDTO handleJsonErrors(HttpMessageNotReadableException ex) {
		if (ex.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException ifx) {
			// Handle Enum errors
			if (ifx.getTargetType() != null && ifx.getTargetType().isEnum()) {
				String invalidValue = ifx.getValue().toString();
				String enumName = ifx.getTargetType().getSimpleName();
				String validValues = Arrays.stream(ifx.getTargetType().getEnumConstants())
						.map(Object::toString)
						.collect(Collectors.joining(", "));
				return new ErrorWithListDTO(
						"Invalid value '" + invalidValue + "' for " + enumName + ". Allowed values: " + validValues,
						LocalDateTime.now(),
						List.of("Allowed values for " + enumName + ": " + validValues));
			}

			// Handle Date/Time errors (Instant, LocalDate, LocalDateTime, etc.)
			if (ifx.getTargetType() != null && (java.time.Instant.class.isAssignableFrom(ifx.getTargetType()) ||
					java.time.LocalDate.class.isAssignableFrom(ifx.getTargetType()) ||
					java.time.LocalDateTime.class.isAssignableFrom(ifx.getTargetType()) ||
					java.time.ZonedDateTime.class.isAssignableFrom(ifx.getTargetType()))) {
				String fieldName = ifx.getPath().isEmpty() ? "unknown field"
						: ifx.getPath().get(ifx.getPath().size() - 1).getFieldName();
				return new ErrorWithListDTO("Invalid date format", LocalDateTime.now(),
						List.of(fieldName + ": Invalid date format. Expected ISO-8601 (2023-12-31T23:59:59Z)"));
			}
		}
		return new ErrorWithListDTO("Malformed JSON request", LocalDateTime.now(), List.of(ex.getMessage()));
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
	public ErrorWithListDTO handleBadRequest(ValidationException ex) {
		return new ErrorWithListDTO(ex.getMessage(), LocalDateTime.now(), ex.getErrorsList());
	}

	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
	public ErrorDTO handleUnauthorized(UnauthorizedException ex) {
		return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN) // 403
	public ErrorDTO handleForbidden(AuthorizationDeniedException ex) {
		return new ErrorDTO("You do not have permission to access this resource", LocalDateTime.now());
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND) // 404
	public ErrorDTO handleNotFound(NotFoundException ex) {
		return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
	public ErrorDTO handleGenericError(Exception ex) {
		logger.error("Internal Server Error", ex);
		return new ErrorDTO("Internal Server Error", LocalDateTime.now());
	}
}
