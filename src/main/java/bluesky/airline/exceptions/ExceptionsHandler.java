package bluesky.airline.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import bluesky.airline.dto.errors.ErrorWithListDTO;
import bluesky.airline.dto.errors.ErrorDTO;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Handler for exceptions
@RestControllerAdvice
public class ExceptionsHandler {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionsHandler.class);

	@ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorWithListDTO handleValidationErrors(org.springframework.web.bind.MethodArgumentNotValidException ex) {
		java.util.List<String> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.toList();
		return new ErrorWithListDTO("Validation errors", LocalDateTime.now(), errors);
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
