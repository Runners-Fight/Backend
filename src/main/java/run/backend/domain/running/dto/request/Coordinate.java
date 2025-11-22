package run.backend.domain.running.dto.request;

import java.time.LocalDateTime;

public record Coordinate(
        double latitude,
        double longitude,
        LocalDateTime timestamp
) {
}
