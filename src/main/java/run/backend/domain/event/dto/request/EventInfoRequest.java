package run.backend.domain.event.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import run.backend.domain.event.enums.RepeatCycle;
import run.backend.domain.event.enums.WeekDay;

public record EventInfoRequest(
    @Schema(description = "일정 제목")
    String title,
    
    @Schema(description = "일정 날짜")
    LocalDate baseDate,
    
    @Schema(description = "반복 주기", example = "NONE / WEEKLY")
    RepeatCycle repeatCycle,
    
    @Schema(description = "반복 요일", example = "MONDAY / TUESDAY / WEDNESDAY / THURSDAY / FRIDAY / SATURDAY / SUNDAY", nullable = true)
    WeekDay repeatDays,
    
    @Schema(description = "시작 시간")
    LocalTime startTime,
    
    @Schema(description = "종료 시간")
    LocalTime endTime,
    
    @Schema(description = "장소")
    String place,
    
    @Schema(description = "러닝캡틴 ID", example = "1")
    Long runningCaptainId
) {
}
