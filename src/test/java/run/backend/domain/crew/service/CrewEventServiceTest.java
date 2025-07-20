package run.backend.domain.crew.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import run.backend.domain.crew.dto.common.DayStatusDto;
import run.backend.domain.crew.dto.response.CrewMonthlyCanlendarResponse;
import run.backend.domain.crew.dto.response.CrewUpcomingEventResponse;
import run.backend.domain.crew.dto.response.CrewWeeklyEventResponse;
import run.backend.domain.crew.dto.response.EventProfileResponse;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.event.entity.Event;
import run.backend.domain.event.enums.RunningStatus;
import run.backend.domain.event.mapper.EventMapper;
import run.backend.domain.event.mapper.EventStatusMapper;
import run.backend.domain.event.repository.EventRepository;
import run.backend.global.dto.DateRange;
import run.backend.global.util.DateRangeUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("CrewEvent 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class CrewEventServiceTest {

    @InjectMocks
    private CrewEventService crewEventService;

    @Mock
    private DateRangeUtil dateRangeUtil;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventStatusMapper eventStatusMapper;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private Crew crew;

    @Test
    @DisplayName("이번주 일정을 정리해서 반환")
    void shouldReturnWeeklyEvent() {

        // given
        LocalDate startOfWeek = LocalDate.of(2025, 7, 14); // 월요일
        LocalDate endOfWeek = LocalDate.of(2025, 7, 20);  // 일요일
        DateRange weekRange = new DateRange(startOfWeek, endOfWeek);

        List<Event> events = List.of(mock(Event.class));
        Map<DayOfWeek, DayStatusDto> statusMap = Map.of(
                DayOfWeek.MONDAY, new DayStatusDto(RunningStatus.SCHEDULED, 1L)
        );

        when(dateRangeUtil.getWeekRange(any(LocalDate.class))).thenReturn(weekRange);
        when(eventRepository.findAllByCrewAndDateBetween(crew, startOfWeek, endOfWeek)).thenReturn(events);
        when(eventStatusMapper.toWeeklyStatus(eq(events), any(LocalDate.class))).thenReturn(statusMap);

        // when
        CrewWeeklyEventResponse response = crewEventService.getCrewWeeklyEvent(crew);

        // then
        assertThat(response.currentDay()).isBetween(1, 7);
        assertThat(response.weeklyRunningStatus()).isEqualTo(statusMap);

        // verify
        verify(dateRangeUtil).getWeekRange(any(LocalDate.class));
        verify(eventRepository).findAllByCrewAndDateBetween(crew, startOfWeek, endOfWeek);
        verify(eventStatusMapper).toWeeklyStatus(eq(events), any(LocalDate.class));

    }

    @Test
    @DisplayName("이번달 일정을 정리해서 반환")
    void shouldReturnMonthlyEvent() {

        // given
        int year = 2025;
        int month = 7;
        LocalDate start = LocalDate.of(2025, 7, 1);
        LocalDate end = LocalDate.of(2025, 7, 31);
        DateRange dateRange = new DateRange(start, end);

        List<Event> events = List.of(mock(Event.class));
        Map<Integer, DayStatusDto> statusMap = Map.of(
                5, new DayStatusDto(RunningStatus.SCHEDULED, 1L)
        );

        when(dateRangeUtil.getMonthRange(year, month)).thenReturn(dateRange);
        when(eventRepository.findAllByCrewAndDateBetween(crew, start, end)).thenReturn(events);
        when(eventStatusMapper.toMonthlyStatus(eq(events), any(LocalDate.class), eq(31))).thenReturn(statusMap);

        // when
        CrewMonthlyCanlendarResponse response = crewEventService.getCrewMonthlyCalendar(crew, year, month);

        // then
        assertThat(response.monthlyRunningStatus()).isEqualTo(statusMap);

        // verify
        verify(dateRangeUtil).getMonthRange(year, month);
        verify(eventRepository).findAllByCrewAndDateBetween(crew, start, end);
        verify(eventStatusMapper).toMonthlyStatus(eq(events), any(LocalDate.class), eq(31));
    }

    @Test
    @DisplayName("다가오는 일정을 정리해서 반환")
    void shouldReturnUpcomingEvent() {

        // given
        List<Event> events = List.of(mock(Event.class));
        List<EventProfileResponse> eventProfiles = List.of(
                new EventProfileResponse(1L, "달리기", null, null, null, 1L)
        );

        when(eventRepository.findAllByCrewAndDateAfter(eq(crew), any(LocalDate.class))).thenReturn(events);
        when(eventMapper.toEventProfileList(events)).thenReturn(eventProfiles);

        // when
        CrewUpcomingEventResponse response = crewEventService.getCrewUpcomingEvent(crew);

        // then
        assertThat(response.eventProfiles()).isEqualTo(eventProfiles);

        // verify
        verify(eventRepository).findAllByCrewAndDateAfter(eq(crew), any(LocalDate.class));
        verify(eventMapper).toEventProfileList(events);
    }

}
