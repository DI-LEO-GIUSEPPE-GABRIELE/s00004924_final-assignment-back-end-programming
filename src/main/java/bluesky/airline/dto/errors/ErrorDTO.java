package bluesky.airline.dto.errors;

import java.time.LocalDateTime;

// DTO for Error responses
public record ErrorDTO(String message, LocalDateTime timestamp) {
}
