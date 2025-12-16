package bluesky.airline.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import bluesky.airline.dto.errors.ErrorWithListDTO;
import bluesky.airline.dto.errors.ErrorDTO;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionsHandler {

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
		return new ErrorDTO("Non hai i permessi per accedere alla risorsa", LocalDateTime.now());
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND) // 404
	public ErrorDTO handleNotFound(NotFoundException ex) {
		return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
	public ErrorDTO handleGenericError(Exception ex) {
		ex.printStackTrace();
		return new ErrorDTO("C'è stato un errore lato server, lo risolveremo presto", LocalDateTime.now());
	}
}
