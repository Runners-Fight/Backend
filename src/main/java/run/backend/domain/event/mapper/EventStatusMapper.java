package run.backend.domain.event.mapper;

import org.springframework.stereotype.Component;
import run.backend.domain.crew.dto.common.DayStatusDto;
import run.backend.domain.event.entity.Event;
import run.backend.domain.event.enums.RunningStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventStatusMapper {

    public Map<DayOfWeek, DayStatusDto> toWeeklyStatus(List<Event> events, LocalDate today) {

        // MON ~ SUN 초기화
        Map<DayOfWeek, DayStatusDto> statusMap = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            statusMap.put(day, new DayStatusDto(RunningStatus.NONE, null));
        }

        // Running Status 바꿔주기
        for (Event event : events) {

            LocalDate eventDate = event.getDate();
            DayOfWeek day = eventDate.getDayOfWeek();

            RunningStatus status = eventDate.isBefore(today)
                    ? RunningStatus.DONE
                    : RunningStatus.SCHEDULED;
            statusMap.put(day, new DayStatusDto(status, event.getId()));
        }
        return statusMap;
    }

    public Map<Integer, DayStatusDto> toMonthlyStatus(List<Event> events, LocalDate today, int endDate) {

        // 1~endDate까지 초기화
        Map<Integer, DayStatusDto> statusMap = new HashMap<>();
        for (int i = 1; i <= endDate; i++) {
            statusMap.put(i, new DayStatusDto(RunningStatus.NONE, null));
        }

        // Running Status 바꿔주기
        for (Event event : events) {

            LocalDate eventDate = event.getDate();
            int day = eventDate.getDayOfMonth();

            RunningStatus status = eventDate.isBefore(today)
                    ? RunningStatus.DONE
                    : RunningStatus.SCHEDULED;
            statusMap.put(day, new DayStatusDto(status, event.getId()));
        }
        return statusMap;
    }
}
