package run.backend.domain.event.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import run.backend.domain.event.enums.RepeatCycle;
import run.backend.domain.event.enums.WeekDay;

public record EventInfoRequest(
    String title,
    LocalDate baseDate,
    @Schema(description = "반복 주기", example = "NONE / WEEKLY")
    RepeatCycle repeatCycle,
    @Schema(description = "반복 요일", example = "MONDAY / TUESDAY / WEDNESDAY / THURSDAY / FRIDAY / SATURDAY / SUNDAY", nullable = true)
    WeekDay repeatDays,
    LocalTime startTime,
    LocalTime endTime,
    String place,
    @Schema(description = "러닝캡틴 ID", example = "1")
    Long runningCaptainId
) {
}
