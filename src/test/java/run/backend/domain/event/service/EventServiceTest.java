package run.backend.domain.event.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.repository.JoinCrewRepository;
import run.backend.domain.event.dto.request.EventInfoRequest;
import run.backend.domain.event.dto.response.EventCreationValidationDto;
import run.backend.domain.event.entity.Event;
import run.backend.domain.event.entity.JoinEvent;
import run.backend.domain.event.entity.PeriodicEvent;
import run.backend.domain.event.enums.RepeatCycle;
import run.backend.domain.event.enums.WeekDay;
import run.backend.domain.event.exception.EventException.InvalidEventCreationRequest;
import run.backend.domain.event.mapper.EventMapper;
import run.backend.domain.event.repository.EventRepository;
import run.backend.domain.event.repository.JoinEventRepository;
import run.backend.domain.event.repository.PeriodicEventRepository;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Gender;
import run.backend.domain.member.enums.OAuthType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("EventService")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PeriodicEventRepository periodicEventRepository;

    @Mock
    private JoinCrewRepository joinCrewRepository;

    @Mock
    private JoinEventRepository joinEventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService sut; // System Under Test

    private Member requestMember;
    private Member runningCaptain;
    private Crew crew;
    private Event savedEvent;
    private JoinEvent savedJoinEvent;
    private PeriodicEvent savedPeriodicEvent;

    @BeforeEach
    void setUp() {
        requestMember = createMember("요청자");
        runningCaptain = createMember("러닝캡틴");
        crew = createCrew("테스트크루");
        savedEvent = createEvent();
        savedJoinEvent = createJoinEvent();
        savedPeriodicEvent = createPeriodicEvent();
    }

    @Nested
    @DisplayName("createEvent 메서드는")
    class CreateEventTest {

        @Test
        @DisplayName("일반 일정 생성 시 Event와 JoinEvent를 저장한다")
        void shouldCreateSingleEventSuccessfully() {
            // given
            EventInfoRequest request = createSingleEventRequest();
            EventCreationValidationDto validation = new EventCreationValidationDto(crew, runningCaptain);

            given(joinCrewRepository.validateEventCreation(any(), any(), any()))
                .willReturn(Optional.of(validation));

            given(eventMapper.toEvent(any(EventInfoRequest.class), any(Crew.class), any(Member.class)))
                .willReturn(savedEvent);

            given(eventMapper.toJoinEvent(any(Event.class), any(Member.class)))
                .willReturn(savedJoinEvent);

            given(eventRepository.save(any(Event.class)))
                .willReturn(savedEvent);

            given(joinEventRepository.save(any(JoinEvent.class)))
                .willReturn(savedJoinEvent);

            // when
            sut.createEvent(request, requestMember);

            // then
            then(eventRepository).should().save(any(Event.class));
            then(joinEventRepository).should().save(any(JoinEvent.class));
            then(periodicEventRepository).should(never()).save(any(PeriodicEvent.class));
        }

        @Test
        @DisplayName("주기적 일정 생성 시 PeriodicEvent, Event, JoinEvent를 모두 저장한다")
        void shouldCreatePeriodicEventSuccessfully() {
            // given
            EventInfoRequest request = createPeriodicEventRequest();
            EventCreationValidationDto validation = new EventCreationValidationDto(crew, runningCaptain);

            given(joinCrewRepository.validateEventCreation(any(), any(), any()))
                .willReturn(Optional.of(validation));

            given(eventMapper.toPeriodicEvent(any(EventInfoRequest.class), any(Crew.class), any(Member.class)))
                .willReturn(savedPeriodicEvent);

            given(eventMapper.toEvent(any(EventInfoRequest.class), any(Crew.class), any(Member.class)))
                .willReturn(savedEvent);

            given(eventMapper.toJoinEvent(any(Event.class), any(Member.class)))
                .willReturn(savedJoinEvent);

            given(periodicEventRepository.save(any(PeriodicEvent.class)))
                .willReturn(savedPeriodicEvent);

            given(eventRepository.save(any(Event.class)))
                .willReturn(savedEvent);

            given(joinEventRepository.save(any(JoinEvent.class)))
                .willReturn(savedJoinEvent);

            // when
            sut.createEvent(request, requestMember);

            // then
            then(periodicEventRepository).should().save(any(PeriodicEvent.class));
            then(eventRepository).should().save(any(Event.class));
            then(joinEventRepository).should().save(any(JoinEvent.class));
        }

        @Test
        @DisplayName("유효하지 않은 요청 시 InvalidEventCreationRequest 예외를 발생시킨다")
        void shouldThrowExceptionWhenValidationFails() {
            // given
            EventInfoRequest request = createSingleEventRequest();

            given(joinCrewRepository.validateEventCreation(any(), any(), any()))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.createEvent(request, requestMember))
                .isInstanceOf(InvalidEventCreationRequest.class);

            then(eventRepository).should(never()).save(any(Event.class));
            then(joinEventRepository).should(never()).save(any(JoinEvent.class));
            then(periodicEventRepository).should(never()).save(any(PeriodicEvent.class));
        }

        @Test
        @DisplayName("일정 생성 후 러닝캡틴이 자동으로 참가 처리된다")
        void shouldAutoJoinRunningCaptainToEvent() {
            // given
            EventInfoRequest request = createSingleEventRequest();
            EventCreationValidationDto validation = new EventCreationValidationDto(crew, runningCaptain);

            given(joinCrewRepository.validateEventCreation(any(), any(), any()))
                .willReturn(Optional.of(validation));

            given(eventMapper.toEvent(any(EventInfoRequest.class), any(Crew.class), any(Member.class)))
                .willReturn(savedEvent);

            given(eventMapper.toJoinEvent(any(Event.class), any(Member.class)))
                .willReturn(savedJoinEvent);

            given(eventRepository.save(any(Event.class)))
                .willReturn(savedEvent);

            given(joinEventRepository.save(any(JoinEvent.class)))
                .willReturn(savedJoinEvent);

            // when
            sut.createEvent(request, requestMember);

            // then
            then(joinEventRepository).should().save(any(JoinEvent.class));
        }
    }

    private Member createMember(String nickname) {
        return Member.builder()
            .username("test_user")
            .nickname(nickname)
            .gender(Gender.MALE)
            .age(25)
            .oauthId("oauth_id")
            .oauthType(OAuthType.GOOGLE)
            .profileImage("profile.jpg")
            .build();
    }

    private Crew createCrew(String name) {
        return Crew.builder()
            .name(name)
            .description("테스트 크루 설명")
            .image("crew.jpg")
            .build();
    }

    private Event createEvent() {
        return Event.builder()
            .title("테스트 일정")
            .date(LocalDate.of(2025, 7, 18))
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .place("테스트 장소")
            .crew(crew)
            .member(runningCaptain)
            .build();
    }

    private JoinEvent createJoinEvent() {
        return JoinEvent.builder()
            .event(savedEvent)
            .member(runningCaptain)
            .build();
    }

    private PeriodicEvent createPeriodicEvent() {
        return PeriodicEvent.builder()
            .baseDate(LocalDate.of(2025, 7, 18))
            .repeatCycle(RepeatCycle.WEEKLY)
            .repeatDays(WeekDay.MONDAY)
            .crew(crew)
            .member(runningCaptain)
            .build();
    }

    private EventInfoRequest createSingleEventRequest() {
        return new EventInfoRequest(
            "테스트 일정",
            LocalDate.of(2025, 7, 18),
            RepeatCycle.NONE,
            null,
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            "테스트 장소",
            1L
        );
    }

    private EventInfoRequest createPeriodicEventRequest() {
        return new EventInfoRequest(
            "주기적 일정",
            LocalDate.of(2025, 7, 18),
            RepeatCycle.WEEKLY,
            WeekDay.MONDAY,
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            "테스트 장소",
            1L
        );
    }
}