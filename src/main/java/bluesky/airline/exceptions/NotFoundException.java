package bluesky.airline.exceptions;

import java.util.UUID;

// Exception for not found resources
public class NotFoundException extends RuntimeException {
	public NotFoundException(UUID id) {
		super("Resource with id " + id + " not found");
	}

	public NotFoundException(String message) {
		super(message);
	}
}
