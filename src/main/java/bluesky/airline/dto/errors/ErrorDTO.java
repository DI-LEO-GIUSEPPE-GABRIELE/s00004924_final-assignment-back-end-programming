package bluesky.airline.dto.errors;

import java.time.LocalDateTime;

public record ErrorDTO(String message, LocalDateTime timestamp) {
}
