package bluesky.airline.dto.errors;

import java.time.LocalDateTime;
import java.util.List;

// DTO for Error responses with a list of errors
public record ErrorWithListDTO(String message, LocalDateTime timestamp, List<String> errorsList) {
}
