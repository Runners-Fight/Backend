package run.backend.domain.event.mapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import run.backend.domain.crew.dto.common.DayStatusDto;
import run.backend.domain.event.entity.Event;
import run.backend.domain.event.enums.RunningStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Event Status Mapper 테스트")
@ExtendWith(MockitoExtension.class)
public class EventStatusMapperTest {

    @InjectMocks
    private EventStatusMapper eventStatusMapper;

    private Event pastEvent;

    private Event futureEvent;

    private final LocalDate today = LocalDate.of(2025, 7, 18);  // 금요일 (5)

    @BeforeEach
    public void setup() {

        pastEvent = Event.builder()
                .date(LocalDate.of(2025, 7, 17))  // 목요일 (4)
                .build();

        futureEvent = Event.builder()
                .date(LocalDate.of(2025, 7, 19))  // 토요일 (6)
                .build();
    }

    @Nested
    @DisplayName("toWeeklyStatus 메서드는")
    class toWeeklyStatusTest {

        @Test
        @DisplayName("요일별 상태가 7개 모두 반환된다")
        void returnsSevenDayStatusEntriesForFullWeek() {

            // given
            List<Event> events = new ArrayList<>();

            // when
            Map<DayOfWeek, DayStatusDto> response = eventStatusMapper.toWeeklyStatus(events, today);

            // then
            assertThat(response.size()).isEqualTo(7);
        }

        @Test
        @DisplayName("이벤트가 없으면 NONE")
        void shouldBeNone_whenEventEmpty() {

            // given
            List<Event> events = new ArrayList<>();

            // when
            Map<DayOfWeek, DayStatusDto> response = eventStatusMapper.toWeeklyStatus(events, today);

            // then
            assertThat(response.size()).isEqualTo(7);

            for (DayStatusDto dto : response.values()) {
                assertThat(dto.status()).isEqualTo(RunningStatus.NONE);
                assertThat(dto.eventId()).isNull();
            }

        }

        @Test
        @DisplayName("과거 이벤트이면 DONE")
        void shouldBeDone_whenPastEvent() {

            // when
            Map<DayOfWeek, DayStatusDto> response = eventStatusMapper.toWeeklyStatus(List.of(pastEvent), today);

            // then
            assertThat(response.size()).isEqualTo(7);

            DayOfWeek dayOfPastEvent = pastEvent.getDate().getDayOfWeek();
            DayStatusDto statusDto = response.get(dayOfPastEvent);
            assertThat(statusDto.status()).isEqualTo(RunningStatus.DONE);
        }

        @Test
        @DisplayName("미래 이벤트이면 SCHEDULED")
        void shouldBeSCHEDULED_whenFutureEvent() {

            // when
            Map<DayOfWeek, DayStatusDto> response = eventStatusMapper.toWeeklyStatus(List.of(futureEvent), today);

            // then
            assertThat(response.size()).isEqualTo(7);

            DayOfWeek dayOfPastEvent = futureEvent.getDate().getDayOfWeek();
            DayStatusDto statusDto = response.get(dayOfPastEvent);
            assertThat(statusDto.status()).isEqualTo(RunningStatus.SCHEDULED);
        }
    }

    @Nested
    @DisplayName("toMonthlyStatus 메서드는")
    class toMonthlyStatusTest {

        private final LocalDate today = LocalDate.of(2025, 7, 18);  // 18일 (금요일)
        private final int endDate = 31;

        @Test
        @DisplayName("이벤트가 없으면 모든 날짜가 NONE")
        void shouldBeNone_whenNoEvent() {
            // given
            List<Event> events = new ArrayList<>();

            // when
            Map<Integer, DayStatusDto> result = eventStatusMapper.toMonthlyStatus(events, today, endDate);

            // then
            assertThat(result).hasSize(endDate);
            for (int i = 1; i <= endDate; i++) {
                DayStatusDto status = result.get(i);
                assertThat(status.status()).isEqualTo(RunningStatus.NONE);
                assertThat(status.eventId()).isNull();
            }
        }

        @Test
        @DisplayName("과거 이벤트가 있으면 DONE 상태가 된다")
        void shouldBeDone_whenEventInPast() {

            // when
            Map<Integer, DayStatusDto> result = eventStatusMapper.toMonthlyStatus(List.of(pastEvent), today, endDate);

            // then
            DayStatusDto status = result.get(17);
            assertThat(status.status()).isEqualTo(RunningStatus.DONE);
        }

        @Test
        @DisplayName("미래 이벤트가 있으면 SCHEDULED 상태가 된다")
        void shouldBeScheduled_whenEventInFuture() {

            // when
            Map<Integer, DayStatusDto> result = eventStatusMapper.toMonthlyStatus(List.of(futureEvent), today, endDate);

            // then
            DayStatusDto status = result.get(19);
            assertThat(status.status()).isEqualTo(RunningStatus.SCHEDULED);
        }
    }
}
