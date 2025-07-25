package run.backend.domain.event.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import run.backend.domain.event.enums.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

public record EventDetailResponse(
        String title,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startDateTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endDateTime,
        String startLocation,
        EventStatus status,
        String distanceKm,
        String runningTime,
        Long runningLeaderId,
        List<ParticipantDto> participants
) {
}
