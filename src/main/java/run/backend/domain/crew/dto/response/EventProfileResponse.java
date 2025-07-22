package run.backend.domain.crew.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record EventProfileResponse(

        Long eventId,
        String title,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        Long participants
) {
}
