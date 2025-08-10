package run.backend.domain.event.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
import org.springframework.test.util.ReflectionTestUtils;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.crew.repository.JoinCrewRepository;
import run.backend.domain.event.dto.request.EventInfoRequest;
import run.backend.domain.event.dto.response.EventCreationValidationDto;
import run.backend.domain.event.entity.Event;
import run.backend.domain.event.entity.JoinEvent;
import run.backend.domain.event.entity.PeriodicEvent;
import run.backend.domain.event.enums.RepeatCycle;
import run.backend.domain.event.enums.WeekDay;
import run.backend.domain.event.exception.EventException.AlreadyJoinedEvent;
import run.backend.domain.event.exception.EventException.EventNotFound;
import run.backend.domain.event.exception.EventException.InvalidEventCreationRequest;
import run.backend.domain.event.exception.EventException.JoinEventNotFound;
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
    private Event completedEvent;
    private JoinEvent savedJoinEvent;
    private PeriodicEvent savedPeriodicEvent;

    @BeforeEach
    void setUp() {
        requestMember = createMemberWithId(1L, "요청자");
        runningCaptain = createMemberWithId(2L, "러닝캡틴");
        crew = createCrew("테스트크루");
        savedEvent = createEvent();
        completedEvent = createEvent();
        completedEvent.complete();
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
            EventCreationValidationDto validation = new EventCreationValidationDto(crew,
                runningCaptain);

            given(joinCrewRepository.validateEventCreation(any(), any(), any()))
                .willReturn(Optional.of(validation));

            given(eventMapper.toEvent(any(EventInfoRequest.class), any(Crew.class),
                any(Member.class)))
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
            EventCreationValidationDto validation = new EventCreationValidationDto(crew,
                runningCaptain);

            given(joinCrewRepository.validateEventCreation(any(), any(), any()))
                .willReturn(Optional.of(validation));

            given(eventMapper.toPeriodicEvent(any(EventInfoRequest.class), any(Crew.class),
                any(Member.class)))
                .willReturn(savedPeriodicEvent);

            given(eventMapper.toEvent(any(EventInfoRequest.class), any(Crew.class),
                any(Member.class)))
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
            EventCreationValidationDto validation = new EventCreationValidationDto(crew,
                runningCaptain);

            given(joinCrewRepository.validateEventCreation(any(), any(), any()))
                .willReturn(Optional.of(validation));

            given(eventMapper.toEvent(any(EventInfoRequest.class), any(Crew.class),
                any(Member.class)))
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

    @Nested
    @DisplayName("updateEvent 메서드는")
    class UpdateEventTest {

        @Test
        @DisplayName("기본 정보만 수정할 때 성공한다")
        void shouldUpdateBasicInfoSuccessfully() {
            // given
            EventInfoRequest request = createUpdateEventRequest(null, RepeatCycle.NONE, null, "변경된 제목");

            given(eventRepository.findById(1L)).willReturn(Optional.of(savedEvent));
            given(periodicEventRepository.findByCrewAndTitleAndTime(any(), any(), any(), any()))
                .willReturn(Optional.empty());

            // when
            sut.updateEvent(1L, request);

            // then
            then(eventRepository).should().findById(1L);
            then(joinEventRepository).should(never()).delete(any());
        }

        @Test
        @DisplayName("이벤트 필드가 실제로 업데이트되는지 확인한다")
        void shouldActuallyUpdateEventFields() {
            // given
            EventInfoRequest request = new EventInfoRequest(
                "변경된 제목",
                LocalDate.of(2025, 7, 20),
                RepeatCycle.NONE,
                null,
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                "변경된 장소",
                null
            );

            given(eventRepository.findById(1L)).willReturn(Optional.of(savedEvent));
            given(periodicEventRepository.findByCrewAndTitleAndTime(any(), any(), any(), any()))
                .willReturn(Optional.empty());

            // when
            sut.updateEvent(1L, request);

            // then
            assertThat(savedEvent.getTitle()).isEqualTo("변경된 제목");
            assertThat(savedEvent.getDate()).isEqualTo(LocalDate.of(2025, 7, 20));
            assertThat(savedEvent.getStartTime()).isEqualTo(LocalTime.of(14, 0));
            assertThat(savedEvent.getEndTime()).isEqualTo(LocalTime.of(15, 0));
            assertThat(savedEvent.getPlace()).isEqualTo("변경된 장소");
        }

        @Test
        @DisplayName("러닝캡틴 변경 시 JoinEvent를 교체한다")
        void shouldChangeRunningCaptainSuccessfully() {
            // given
            Member newRunningCaptain = createMemberWithId(3L, "새러닝캡틴");

            EventInfoRequest request = createUpdateEventRequest(3L, RepeatCycle.NONE, null, "변경된 제목");

            given(eventRepository.findById(1L)).willReturn(Optional.of(savedEvent));
            given(joinCrewRepository.findCrewMemberById(3L, crew.getId(), JoinStatus.APPROVED))
                .willReturn(Optional.of(newRunningCaptain));
            given(joinEventRepository.findByEventAndMember(savedEvent, runningCaptain))
                .willReturn(Optional.of(savedJoinEvent));
            given(eventMapper.toJoinEvent(savedEvent, newRunningCaptain)).willReturn(
                savedJoinEvent);
            given(periodicEventRepository.findByCrewAndTitleAndTime(any(), any(), any(), any()))
                .willReturn(Optional.empty());

            // when
            sut.updateEvent(1L, request);

            // then
            then(joinEventRepository).should().findByEventAndMember(savedEvent, runningCaptain);
            then(joinEventRepository).should().delete(savedJoinEvent);
            then(joinEventRepository).should().save(any(JoinEvent.class));
        }

        @Test
        @DisplayName("러닝캡틴이 실제로 변경되는지 확인한다")
        void shouldActuallyChangeRunningCaptain() {
            // given
            Member newCaptain = createMemberWithId(3L, "새 러닝캡틴");

            EventInfoRequest request = createUpdateEventRequest(3L, RepeatCycle.NONE, null, "변경된 제목");

            given(eventRepository.findById(1L)).willReturn(Optional.of(savedEvent));
            given(joinCrewRepository.findCrewMemberById(3L, crew.getId(), JoinStatus.APPROVED))
                .willReturn(Optional.of(newCaptain));
            given(joinEventRepository.findByEventAndMember(savedEvent, runningCaptain))
                .willReturn(Optional.of(savedJoinEvent));
            given(eventMapper.toJoinEvent(savedEvent, newCaptain)).willReturn(savedJoinEvent);
            given(periodicEventRepository.findByCrewAndTitleAndTime(any(), any(), any(), any()))
                .willReturn(Optional.empty());

            // when
            sut.updateEvent(1L, request);

            // then
            then(joinEventRepository).should().delete(savedJoinEvent);
            assertThat(savedEvent.getMember()).isEqualTo(newCaptain);
        }

        @Test
        @DisplayName("반복 설정을 추가할 때 PeriodicEvent를 생성한다")
        void shouldAddPeriodicEventSuccessfully() {
            // given
            EventInfoRequest request = createUpdateEventRequest(null, RepeatCycle.WEEKLY, WeekDay.TUESDAY, "반복 일정으로 변경");
            EventInfoRequest mappedRequest = createPeriodicEventRequest();

            given(eventRepository.findById(1L)).willReturn(Optional.of(savedEvent));
            given(periodicEventRepository.findByCrewAndTitleAndTime(any(), any(), any(), any()))
                .willReturn(Optional.empty());
            given(eventMapper.toEventInfoRequest(request, savedEvent)).willReturn(mappedRequest);
            given(eventMapper.toPeriodicEvent(mappedRequest, crew, runningCaptain)).willReturn(
                savedPeriodicEvent);

            // when
            sut.updateEvent(1L, request);

            // then
            then(periodicEventRepository).should().save(any(PeriodicEvent.class));
        }

        @Test
        @DisplayName("반복 설정을 제거할 때 기존 PeriodicEvent를 soft delete한다")
        void shouldRemovePeriodicEventSuccessfully() {
            // given
            EventInfoRequest request = createUpdateEventRequest(null, RepeatCycle.NONE, null, "반복 제거");

            given(eventRepository.findById(1L)).willReturn(Optional.of(savedEvent));
            given(periodicEventRepository.findByCrewAndTitleAndTime(any(), any(), any(), any()))
                .willReturn(Optional.of(savedPeriodicEvent));

            // when
            sut.updateEvent(1L, request);

            // then
            then(periodicEventRepository).should().delete(savedPeriodicEvent);
        }

        @Test
        @DisplayName("존재하지 않는 일정 수정 시 EventNotFound 예외를 발생시킨다")
        void shouldThrowEventNotFoundWhenEventDoesNotExist() {
            // given
            EventInfoRequest request = createUpdateEventRequest(null, RepeatCycle.NONE, null, "변경된 제목");

            given(eventRepository.findById(1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.updateEvent(1L, request))
                .isInstanceOf(EventNotFound.class);
        }

        @Test
        @DisplayName("새로운 러닝캡틴이 크루원이 아닐 때 InvalidEventCreationRequest 예외를 발생시킨다")
        void shouldThrowExceptionWhenNewRunningCaptainIsNotCrewMember() {
            // given
            EventInfoRequest request = createUpdateEventRequest(3L, RepeatCycle.NONE, null, "변경된 제목");

            given(eventRepository.findById(1L)).willReturn(Optional.of(savedEvent));
            given(joinCrewRepository.findCrewMemberById(3L, crew.getId(), JoinStatus.APPROVED))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.updateEvent(1L, request))
                .isInstanceOf(InvalidEventCreationRequest.class);
        }
    }

    private Member createMemberWithId(Long id, String nickname) {
        Member member = Member.builder()
            .username("test_user")
            .nickname(nickname)
            .gender(Gender.MALE)
            .age(25)
            .oauthId("oauth_id_" + id)
            .oauthType(OAuthType.GOOGLE)
            .profileImage("profile.jpg")
            .build();

        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private Crew createCrew(String name) {
        Crew crew = Crew.builder()
            .name(name)
            .description("테스트 크루 설명")
            .image("crew.jpg")
            .build();

        ReflectionTestUtils.setField(crew, "id", 1L);
        return crew;
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

    private EventInfoRequest createUpdateEventRequest(Long runningCaptainId, RepeatCycle repeatCycle, WeekDay weekDay, String title) {
        return new EventInfoRequest(
            title,
            LocalDate.of(2025, 7, 19),
            repeatCycle,
            weekDay,
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            "장소",
            runningCaptainId
        );
    }

    @Nested
    @DisplayName("getEventDetail 메서드는")
    class GetEventTest {
        @Test
        @DisplayName("일정 시작 전에는 예정된 모든 참가자를 조회한다")
        void shouldReturnExpectedParticipantsBeforeEvent() {
            //given
            given(eventRepository.findById(1L)).willReturn(Optional.of(savedEvent));
            given(joinEventRepository.findByEvent(any())).willReturn(List.of(savedJoinEvent));
            given(joinEventRepository.findActualParticipantsByEvent(any())).willReturn(List.of(savedJoinEvent));

            //when
            sut.getEventDetail(1L);

            //then
            then(eventRepository).should().findById(1L);
            then(joinEventRepository).should().findByEvent(any());
            then(joinEventRepository).should(never()).findActualParticipantsByEvent(any());
        }

        @Test
        @DisplayName("일정 완료 후에는 실제 참가한 참가자만 조회한다")
        void shouldReturnActualParticipantsAfterEvent() {
            //given
            given(eventRepository.findById(1L)).willReturn(Optional.of(completedEvent));
            given(joinEventRepository.findByEvent(any())).willReturn(List.of(savedJoinEvent));
            given(joinEventRepository.findActualParticipantsByEvent(any())).willReturn(List.of(savedJoinEvent));

            //when
            sut.getEventDetail(1L);

            //then
            then(eventRepository).should().findById(1L);
            then(joinEventRepository).should(never()).findByEvent(any());
            then(joinEventRepository).should().findActualParticipantsByEvent(any());
        }

        @Test
        @DisplayName("존재하지 않는 일정 조회 시 예외를 던진다")
        void shouldThrowExceptionWhenEventNotFound() {
            //given
            given(eventRepository.findById(1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getEventDetail(1L))
                .isInstanceOf(EventNotFound.class);

            //then
            then(eventRepository).should().findById(1L);
            then(joinEventRepository).should(never()).findByEvent(any());
            then(joinEventRepository).should(never()).findActualParticipantsByEvent(any());
        }
    }

    @Nested
    @DisplayName("joinEvent 메서드는")
    class JoinEventTest {
        @Test
        @DisplayName("일정 참여에 성공한다")
        void shouldJoinEventSuccessfully() {
            //given
            Long initialExpectedParticipants = savedEvent.getExpectedParticipants();
            
            given(eventRepository.findById(1L)).willReturn(Optional.of(savedEvent));
            given(joinEventRepository.save(any())).willReturn(savedJoinEvent);
            given(joinEventRepository.existsByEventAndMember(any(),any())).willReturn(false);
            given(eventMapper.toJoinEvent(any(Event.class), any(Member.class)))
                .willReturn(savedJoinEvent);

            //when
            sut.joinEvent(1L, requestMember);

            //then
            then(eventRepository).should().findById(1L);
            then(joinEventRepository).should().save(any());
            assertThat(savedEvent.getExpectedParticipants()).isEqualTo(initialExpectedParticipants + 1);
        }

        @Test
        @DisplayName("이미 참여한 경우 예외를 던진다")
        void shouldThrowExceptionWhenAlreadyJoined() {
            //given
            Long initialExpectedParticipants = savedEvent.getExpectedParticipants();

            given(eventRepository.findById(1L)).willReturn(Optional.of(savedEvent));
            given(joinEventRepository.existsByEventAndMember(any(),any())).willReturn(true);

            //when & then
            assertThatThrownBy(() -> sut.joinEvent(1L, requestMember))
                .isInstanceOf(AlreadyJoinedEvent.class);
            assertThat(savedEvent.getExpectedParticipants()).isEqualTo(initialExpectedParticipants);

        }

        @Test
        @DisplayName("존재하지 않는 이벤트인 경우 예외를 던진다")
        void shouldThrowExceptionWhenEventNotFound() {
            given(eventRepository.findById(1L)).willReturn(Optional.empty());

            //when & then
            assertThatThrownBy(() -> sut.joinEvent(1L, requestMember))
                .isInstanceOf(EventNotFound.class);
        }
    }

    @Nested
    @DisplayName("cancelEvent 메서드는")
    class CancelEventTest {
        @Test
        @DisplayName("일정 참여 취소에 성공한다")
        void shouldCancelEventSuccessfully() {
            //given
            Long initialExpectedParticipants = savedEvent.getExpectedParticipants();

            given(eventRepository.findById(1L)).willReturn(Optional.of(savedEvent));
            given(joinEventRepository.findByEventAndMember(any(), any())).willReturn(Optional.of(savedJoinEvent));

            //when
            sut.cancelJoinEvent(1L, requestMember);

            //then
            then(eventRepository).should().findById(1L);
            then(joinEventRepository).should().delete(savedJoinEvent);
            assertThat(savedEvent.getExpectedParticipants()).isEqualTo(initialExpectedParticipants - 1);
        }

        @Test
        @DisplayName("사용자가 참여 중인 일정이 아니면 예외를 던진다")
        void shouldThrowExceptionWhenNotJoined() {
            //given
            Long initialExpectedParticipants = savedEvent.getExpectedParticipants();

            given(eventRepository.findById(1L)).willReturn(Optional.of(savedEvent));
            given(joinEventRepository.findByEventAndMember(any(), any())).willReturn(Optional.empty());

            //when & then
            assertThatThrownBy(() -> sut.cancelJoinEvent(1L, requestMember))
                .isInstanceOf(JoinEventNotFound.class);
            assertThat(savedEvent.getExpectedParticipants()).isEqualTo(initialExpectedParticipants);

        }

        @Test
        @DisplayName("존재하지 않는 일정인 경우 예외를 던진다")
        void shouldThrowExceptionWhenEventNotFound() {
            given(eventRepository.findById(1L)).willReturn(Optional.empty());

            //when & then
            assertThatThrownBy(() -> sut.cancelJoinEvent(1L, requestMember))
                .isInstanceOf(EventNotFound.class);
        }
    }
}