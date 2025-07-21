package run.backend.domain.crew.dto.common;

import run.backend.domain.event.enums.RunningStatus;

public record DayStatusDto(
        RunningStatus status,
        Long eventId
) {
}
