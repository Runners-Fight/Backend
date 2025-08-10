package run.backend.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import org.springframework.test.util.ReflectionTestUtils;
import run.backend.domain.crew.dto.response.EventResponseDto;
import run.backend.domain.crew.dto.response.EventProfileResponse;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.entity.JoinCrew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.crew.repository.JoinCrewRepository;
import run.backend.domain.event.entity.JoinEvent;
import run.backend.domain.event.enums.EventStatus;
import run.backend.domain.event.repository.JoinEventRepository;
import run.backend.domain.member.dto.response.MemberCrewStatusResponse;
import run.backend.domain.member.dto.response.MemberInfoResponse;
import run.backend.domain.member.dto.response.MemberParticipatedCountResponse;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Gender;
import run.backend.domain.member.enums.OAuthType;
import run.backend.domain.member.repository.MemberRepository;
import run.backend.global.dto.DateRange;
import run.backend.global.util.DateRangeUtil;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService")
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JoinCrewRepository joinCrewRepository;

    @Mock
    private JoinEventRepository joinEventRepository;

    @Mock
    private DateRangeUtil dateRangeUtil;

    @InjectMocks
    private MemberService sut; // System Under Test

    private Member testMember;
    private Crew testCrew;

    @BeforeEach
    public void setUp() {

        // member
        testMember = createMemberWithId(1L, "테스트사용자");

        // crew
        testCrew = createCrew("테스트크루");
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

    @Test
    @DisplayName("회원 정보 조회 테스트")
    public void getMemberInfoTest() {

        // given
        when(memberRepository.findCrewByMemberIdAndStatus(testMember.getId(), JoinStatus.APPROVED))
                .thenReturn(Optional.of(testCrew));

        // when
        MemberInfoResponse response = sut.getMemberInfo(testMember);

        // then
        assertEquals(testMember.getNickname(), response.nickName());
        assertEquals(testCrew.getName(), response.crewName());
    }

    @Test
    void getMembersCrewExists_shouldReturnNone_whenNoJoinCrew() {

        // Given
        Member member = mock(Member.class);
        when(joinCrewRepository.findByMember(member)).thenReturn(Optional.empty());

        // When
        MemberCrewStatusResponse response = sut.getMembersCrewExists(member);

        // Then
        assertEquals("NONE", response.status());
    }

    @Test
    void getMembersCrewExists_shouldReturnJoinStatus_whenJoinCrewExists() {

        // Given
        Member member = mock(Member.class);
        JoinCrew joinCrew = mock(JoinCrew.class);
        when(joinCrew.getJoinStatus()).thenReturn(JoinStatus.APPROVED);
        when(joinCrewRepository.findByMember(member)).thenReturn(Optional.of(joinCrew));

        // When
        MemberCrewStatusResponse response = sut.getMembersCrewExists(member);

        // Then
        assertEquals("APPROVED", response.status());
    }

    @Nested
    @DisplayName("getParticipatedEventCount 메서드는")
    class GetParticipatedEventCountTest {

        @Test
        @DisplayName("이번 달 참여한 이벤트 개수를 반환한다")
        void shouldReturnMonthlyParticipatedEventCount() {
            // given
            LocalDate today = LocalDate.now();
            DateRange monthRange = new DateRange(
                    today.withDayOfMonth(1),
                    today.withDayOfMonth(today.lengthOfMonth())
            );

            JoinEvent joinEvent1 = createJoinEvent(1L);
            JoinEvent joinEvent2 = createJoinEvent(2L);
            List<JoinEvent> monthlyJoinEvents = List.of(joinEvent1, joinEvent2);

            given(dateRangeUtil.getMonthRange(today.getYear(), today.getMonthValue()))
                    .willReturn(monthRange);
            given(joinEventRepository.findMonthlyParticipatedEvents(
                    testMember, monthRange.start(), monthRange.end(), EventStatus.COMPLETED))
                    .willReturn(monthlyJoinEvents);

            // when
            MemberParticipatedCountResponse response = sut.getParticipatedEventCount(testMember);

            // then
            assertThat(response.participatedCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("참여한 이벤트가 없을 때 0을 반환한다")
        void shouldReturnZeroWhenNoParticipatedEvents() {
            // given
            LocalDate today = LocalDate.now();
            DateRange monthRange = new DateRange(
                    today.withDayOfMonth(1),
                    today.withDayOfMonth(today.lengthOfMonth())
            );

            given(dateRangeUtil.getMonthRange(today.getYear(), today.getMonthValue()))
                    .willReturn(monthRange);
            given(joinEventRepository.findMonthlyParticipatedEvents(
                    testMember, monthRange.start(), monthRange.end(), EventStatus.COMPLETED))
                    .willReturn(List.of());

            // when
            MemberParticipatedCountResponse response = sut.getParticipatedEventCount(testMember);

            // then
            assertThat(response.participatedCount()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("getParticipatedEvent 메서드는")
    class GetParticipatedEventTest {

        @Test
        @DisplayName("이번 달 완료된 이벤트 목록을 반환한다")
        void shouldReturnMonthlyCompletedEvents() {
            // given
            LocalDate today = LocalDate.now();
            DateRange monthRange = new DateRange(
                    today.withDayOfMonth(1),
                    today.withDayOfMonth(today.lengthOfMonth())
            );

            EventProfileResponse eventProfile1 = createEventProfileResponse(1L, "러닝 이벤트 1");
            EventProfileResponse eventProfile2 = createEventProfileResponse(2L, "러닝 이벤트 2");
            List<EventProfileResponse> eventProfiles = List.of(eventProfile1, eventProfile2);

            given(dateRangeUtil.getMonthRange(today.getYear(), today.getMonthValue()))
                    .willReturn(monthRange);
            given(joinEventRepository.findMonthlyCompletedEvents(
                    testMember, monthRange.start(), monthRange.end(), EventStatus.COMPLETED))
                    .willReturn(eventProfiles);

            // when
            EventResponseDto response = sut.getParticipatedEvent(testMember);

            // then
            assertThat(response.eventProfiles()).hasSize(2);
            assertThat(response.eventProfiles()).containsExactly(eventProfile1, eventProfile2);
        }

        @Test
        @DisplayName("완료된 이벤트가 없을 때 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoCompletedEvents() {
            // given
            LocalDate today = LocalDate.now();
            DateRange monthRange = new DateRange(
                    today.withDayOfMonth(1),
                    today.withDayOfMonth(today.lengthOfMonth())
            );

            given(dateRangeUtil.getMonthRange(today.getYear(), today.getMonthValue()))
                    .willReturn(monthRange);
            given(joinEventRepository.findMonthlyCompletedEvents(
                    testMember, monthRange.start(), monthRange.end(), EventStatus.COMPLETED))
                    .willReturn(List.of());

            // when
            EventResponseDto response = sut.getParticipatedEvent(testMember);

            // then
            assertThat(response.eventProfiles()).isEmpty();
        }
    }

    private JoinEvent createJoinEvent(Long eventId) {
        JoinEvent joinEvent = JoinEvent.builder()
                .member(testMember)
                .build();

        ReflectionTestUtils.setField(joinEvent, "id", eventId);
        return joinEvent;
    }

    private EventProfileResponse createEventProfileResponse(Long eventId, String title) {
        return new EventProfileResponse(
                eventId,
                title,
                LocalDate.now(),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                5L
        );
    }
}
