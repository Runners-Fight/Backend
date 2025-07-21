package run.backend.domain.crew.dto.response;

import run.backend.domain.crew.dto.common.DayStatusDto;

import java.time.DayOfWeek;
import java.util.Map;

public record CrewWeeklyEventResponse(
        int currentDay,
        Map<DayOfWeek, DayStatusDto> weeklyRunningStatus

) {
}
