package bluesky.airline.exceptions;

// Exception for forbidden access
public class ForbiddenException extends RuntimeException {
	public ForbiddenException(String message) {
		super(message);
	}
}
