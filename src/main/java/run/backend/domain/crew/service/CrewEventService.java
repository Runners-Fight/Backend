package run.backend.domain.crew.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.backend.domain.crew.dto.common.DayStatusDto;
import run.backend.domain.crew.dto.response.CrewMonthlyCanlendarResponse;
import run.backend.domain.crew.dto.response.CrewUpcomingEventResponse;
import run.backend.domain.crew.dto.response.CrewWeeklyEventResponse;
import run.backend.domain.crew.dto.response.EventProfileResponse;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.event.mapper.EventMapper;
import run.backend.domain.event.mapper.EventStatusMapper;
import run.backend.domain.event.entity.Event;
import run.backend.domain.event.repository.EventRepository;
import run.backend.global.dto.DateRange;
import run.backend.global.util.DateRangeUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewEventService {

    private final DateRangeUtil dateRangeUtil;
    private final EventRepository eventRepository;
    private final EventStatusMapper eventStatusMapper;
    private final EventMapper eventMapper;

    public CrewWeeklyEventResponse getCrewWeeklyEvent(Crew crew) {

        LocalDate today = LocalDate.now();
        DateRange dateRange = dateRangeUtil.getWeekRange(today);

        List<Event> weeklyEvents = eventRepository.findAllByCrewAndDateBetween(crew, dateRange.start(), dateRange.end());
        Map<DayOfWeek, DayStatusDto> statusMap = eventStatusMapper.toWeeklyStatus(weeklyEvents, today);

        return new CrewWeeklyEventResponse(today.getDayOfWeek().getValue(), statusMap);
    }

    public CrewMonthlyCanlendarResponse getCrewMonthlyCalendar(Crew crew, int year, int month) {

        LocalDate today = LocalDate.now();
        DateRange dateRange = dateRangeUtil.getMonthRange(year, month);

        List<Event> events = eventRepository.findAllByCrewAndDateBetween(crew, dateRange.start(), dateRange.end());
        Map<Integer, DayStatusDto> statusMap = eventStatusMapper.toMonthlyStatus(events, today, dateRange.end().getDayOfMonth());

        return new CrewMonthlyCanlendarResponse(statusMap);
    }

    public CrewUpcomingEventResponse getCrewUpcomingEvent(Crew crew) {

        LocalDate today = LocalDate.now();

        List<Event> events = eventRepository.findAllByCrewAndDateAfter(crew, today);
        List<EventProfileResponse> eventProfiles = eventMapper.toEventProfileList(events);

        return new CrewUpcomingEventResponse(eventProfiles);
    }
}
