package run.backend.global.dto;

import java.time.LocalDate;

public record DateRange(
        LocalDate start,
        LocalDate end
) {
}
